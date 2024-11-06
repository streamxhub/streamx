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

package org.apache.streampark.console.core.watcher;

import org.apache.streampark.common.enums.ClusterState;
import org.apache.streampark.common.util.HttpClientUtils;
import org.apache.streampark.console.base.util.JacksonUtils;
import org.apache.streampark.console.core.entity.SparkCluster;
import org.apache.streampark.console.core.metrics.spark.SparkApplicationMetrics;
import org.apache.streampark.console.core.service.SparkClusterService;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.util.Timeout;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * This implementation is currently used for tracing spark cluster state.
 */
@Slf4j
@Component
public class SparkClusterWatcher {

    @Autowired
    private SparkClusterService sparkClusterService;

    private static final Timeout HTTP_TIMEOUT = Timeout.ofSeconds(5L);

    @Qualifier("sparkRestAPIWatchingExecutor")
    @Autowired
    private Executor executorService;

    private Long lastWatchTime = 0L;

    // Track interval every 30 seconds
    private static final Duration WATCHER_INTERVAL = Duration.ofSeconds(30);

    /**
     * Watcher cluster lists
     */
    private static final Map<Long, SparkCluster> WATCHER_CLUSTERS = new ConcurrentHashMap<>(8);

    private static final Cache<Long, ClusterState> FAILED_STATES =
        Caffeine.newBuilder().expireAfterWrite(WATCHER_INTERVAL).build();

    private boolean immediateWatch = false;

    /**
     * Initialize cluster cache
     */
    @PostConstruct
    private void init() {
        WATCHER_CLUSTERS.clear();
        List<SparkCluster> sparkClusters =
            sparkClusterService.list(
                new LambdaQueryWrapper<SparkCluster>()
                    .eq(SparkCluster::getClusterState, ClusterState.RUNNING.getState()));
        sparkClusters.forEach(cluster -> WATCHER_CLUSTERS.put(cluster.getId(), cluster));
    }

    @Scheduled(fixedDelayString = "${job.state-watcher.fixed-delayed:1000}")
    private void start() {
        Long timeMillis = System.currentTimeMillis();
        if (immediateWatch || timeMillis - lastWatchTime >= WATCHER_INTERVAL.toMillis()) {
            lastWatchTime = timeMillis;
            immediateWatch = false;
            WATCHER_CLUSTERS.forEach(
                (aLong, sparkCluster) -> executorService.execute(
                    () -> {
                        ClusterState state = getClusterState(sparkCluster);
                        switch (state) {
                            case FAILED:
                            case LOST:
                            case UNKNOWN:
                            case KILLED:
                                sparkClusterService.updateClusterState(sparkCluster.getId(), state);
                                unWatching(sparkCluster);
                                alert(sparkCluster, state);
                                break;
                            default:
                                break;
                        }
                    }));
        }
    }

    private void alert(SparkCluster cluster, ClusterState state) {

    }

    /**
     * Retrieves the state of a cluster from the Flink or YARN API.
     *
     * @param sparkCluster The SparkCluster object representing the cluster.
     * @return The ClusterState object representing the state of the cluster.
     */
    public ClusterState getClusterState(SparkCluster sparkCluster) {
        ClusterState state = FAILED_STATES.getIfPresent(sparkCluster.getId());
        if (state != null) {
            return state;
        }
        state = httpClusterState(sparkCluster);
        if (ClusterState.isRunning(state)) {
            FAILED_STATES.invalidate(sparkCluster.getId());
        } else {
            immediateWatch = true;
            FAILED_STATES.put(sparkCluster.getId(), state);
        }
        return state;
    }

    /**
     * Retrieves the state of a cluster from the Flink or YARN API using the remote HTTP endpoint.
     *
     * @param sparkCluster The SparkCluster object representing the cluster.
     * @return The ClusterState object representing the state of the cluster.
     */
    private ClusterState httpRemoteClusterState(SparkCluster sparkCluster) {
        try {
            SparkApplicationMetrics metrics = getSparkApplicationMetrics(sparkCluster.getMasterWebUrl());
            return metrics.isRunning() ? ClusterState.RUNNING : ClusterState.LOST;
        } catch (Exception ignored) {
            log.error("cluster id:{} get state from spark json api failed", sparkCluster.getId());
        }
        return ClusterState.LOST;
    }

    public SparkApplicationMetrics getSparkApplicationMetrics(Long clusterId) {
        try {
            return getSparkApplicationMetrics(sparkClusterService.getById(clusterId).getMasterWebUrl());
        } catch (Exception e) {
            log.error("cluster id:{} get state from spark json api failed", clusterId);
            throw e;
        }
    }

    /**
     * get flink cluster state
     *
     * @param sparkCluster
     * @return
     */
    private ClusterState httpClusterState(SparkCluster sparkCluster) {
        switch (sparkCluster.getDeployModeEnum()) {
            case REMOTE:
                return httpRemoteClusterState(sparkCluster);
            default:
                return ClusterState.UNKNOWN;
        }
    }

    public SparkApplicationMetrics getSparkApplicationMetrics(String masterWebUi) {
        String sparkJsonUrl =
            masterWebUi.endsWith("json/")
                ? masterWebUi
                : masterWebUi.concat("/json/");
        String res =
            HttpClientUtils.httpGetRequest(
                sparkJsonUrl,
                RequestConfig.custom().setConnectTimeout(HTTP_TIMEOUT).build());
        try {
            return JacksonUtils.read(res, SparkApplicationMetrics.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * add sparkCluster to watching
     *
     * @param sparkCluster
     */
    public static void addWatching(SparkCluster sparkCluster) {
        if (!WATCHER_CLUSTERS.containsKey(sparkCluster.getId())) {
            log.info("add the cluster with id:{} to watcher cluster cache", sparkCluster.getId());
            WATCHER_CLUSTERS.put(sparkCluster.getId(), sparkCluster);
        }
    }

    /**
     * @param sparkCluster
     */
    public static void unWatching(SparkCluster sparkCluster) {
        if (WATCHER_CLUSTERS.containsKey(sparkCluster.getId())) {
            log.info("remove the cluster with id:{} from watcher cluster cache", sparkCluster.getId());
            WATCHER_CLUSTERS.remove(sparkCluster.getId());
        }
    }

    /**
     * Verify the cluster connection whether is valid.
     *
     * @return <code>false</code> if the connection of the cluster is invalid, <code>true</code> else.
     */
    public Boolean verifyClusterConnection(SparkCluster sparkCluster) {
        ClusterState clusterStateEnum = httpClusterState(sparkCluster);
        return ClusterState.isRunning(clusterStateEnum);
    }
}
