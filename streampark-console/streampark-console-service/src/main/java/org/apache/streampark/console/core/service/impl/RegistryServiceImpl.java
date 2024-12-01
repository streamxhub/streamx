/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.streampark.console.core.service.impl;

import org.apache.streampark.common.util.SystemPropertyUtils;
import org.apache.streampark.console.core.service.DistributedTaskService;
import org.apache.streampark.console.core.service.RegistryService;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

@Slf4j
@Service
public class RegistryServiceImpl implements RegistryService {

    private static final String REGISTRY_PATH = "/services";
    private static final int HEARTBEAT_INTERVAL = 10000;
    private static final int HEARTBEAT_TIMEOUT = 60000;

    private String zkAddress;
    private ZooKeeper zk;
    private String nodePath;

    private final Watcher watcher = event -> {
        if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged
            && event.getPath().equals(REGISTRY_PATH)) {
            handleNodeChanges();
        }
    };

    @Getter
    private Set<String> currentNodes = new HashSet<>();

    @Autowired
    private DistributedTaskService distributedTaskService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public void registry() {
        try {
            zkAddress = SystemPropertyUtils.get("high-availability.zookeeper.quorum", "localhost:2181");
            zk = new ZooKeeper(zkAddress, HEARTBEAT_TIMEOUT, watcher);
            if (zk.exists(REGISTRY_PATH, false) == null) {
                zk.create(REGISTRY_PATH, new byte[0], OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            String ip = InetAddress.getLocalHost().getHostAddress();
            String port = SystemPropertyUtils.get("server.port", "10000");
            String server_id = ip + ":" + port;
            nodePath = zk.create(REGISTRY_PATH + "/" + server_id, new byte[0],
                OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

            currentNodes.add(nodePath);

            doRegister();
        } catch (Exception e) {
            log.error("Failed to init ZooKeeper client", e);
        }
    }

    public void doRegister() {
        try {
            distributedTaskService.init(currentNodes, nodePath);
            startHeartbeat();
            startHeartbeatChecker();
            handleNodeChanges();
            log.info("ZooKeeper client started: {}", nodePath);
        } catch (Exception e) {
            log.error("Failed to start ZooKeeper client", e);
        }
    }

    private void startHeartbeat() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                zk.setData(nodePath, new byte[0], -1);
                log.info("Heartbeat updated for node: {}", nodePath);
            } catch (KeeperException e) {
                log.info("Zookeeper session expired, attempting to reconnect...");
                reconnectAndRegister();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Failed to update heartbeat for node: {}", nodePath, e);
            }
        }, 0, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private void startHeartbeatChecker() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                long now = System.currentTimeMillis();
                List<String> servers = zk.getChildren(REGISTRY_PATH, false);
                for (String server : servers) {
                    String serverPath = REGISTRY_PATH + "/" + server;
                    Stat stat = zk.exists(serverPath, false);
                    if (stat != null && (now - stat.getMtime() > HEARTBEAT_TIMEOUT)) {
                        zk.delete(serverPath, -1);
                        log.info("Deleted stale node: {}", serverPath);
                    }
                }
            } catch (KeeperException e) {
                log.info("Zookeeper session expired, attempting to reconnect...");
                reconnectAndRegister();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Failed to check heartbeat", e);
            }
        }, HEARTBEAT_TIMEOUT, HEARTBEAT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private synchronized void handleNodeChanges() {
        try {
            List<String> nodes = zk.getChildren(REGISTRY_PATH, true);
            Set<String> newNodes = new HashSet<>(nodes);

            for (String node : newNodes) {
                if (!currentNodes.contains(node)) {
                    log.info("Node added: {}", node);
                    distributedTaskService.addServer(node);
                }
            }

            for (String node : currentNodes) {
                if (!newNodes.contains(node)) {
                    log.info("Node removed: {}", node);
                    distributedTaskService.removeServer(node);
                }
            }

            currentNodes = newNodes;
            log.info("Online servers: {}", currentNodes);
        } catch (KeeperException e) {
            log.info("Zookeeper session expired, attempting to reconnect...");
            reconnectAndRegister();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to handle node changes", e);
        }
    }

    private void reconnectAndRegister() {
        int retries = 5;
        while (retries > 0) {
            try {
                zk.close();
                zk = new ZooKeeper(zkAddress, HEARTBEAT_TIMEOUT, watcher);
                zk.create(nodePath, new byte[0], OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                return;
            } catch (Exception e) {
                retries--;
                log.warn("Retrying connection, attempts left: {}", retries, e);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        log.error("Failed to reconnect and register node after multiple attempts.");
    }

    @Override
    public void unRegister() {
        try {
            zk.close();
            scheduler.shutdown();
            log.info("ZooKeeper client closed: {}", nodePath);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to close ZooKeeper client", e);
        }
    }
}
