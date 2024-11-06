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

package org.apache.streampark.console.core.service;

import org.apache.streampark.common.enums.ClusterState;
import org.apache.streampark.common.enums.SparkDeployMode;
import org.apache.streampark.console.base.domain.RestRequest;
import org.apache.streampark.console.core.bean.ResponseResult;
import org.apache.streampark.console.core.entity.SparkCluster;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

/** Spark Cluster Service, Provides control over the cluster */
public interface SparkClusterService extends IService<SparkCluster> {

    /**
     * List all currently available clusters
     *
     * @return List of spark cluster
     */
    List<SparkCluster> listAvailableCluster();

    /**
     * Check the spark cluster status
     *
     * @param sparkCluster SparkCluster To be check
     * @return The response value
     */
    ResponseResult check(SparkCluster sparkCluster);

    /**
     * Create spark cluster
     *
     * @param sparkCluster SparkCluster to be create
     * @return Whether the creation is successful
     */
    Boolean create(SparkCluster sparkCluster, Long userId);

    /**
     * Remove spark cluster
     *
     * @param id SparkCluster id whitch to be removed
     */
    void remove(Long id);

    /**
     * Update spark cluster
     *
     * @param sparkCluster SparkCluster to be update
     */
    void update(SparkCluster sparkCluster);

    /**
     * Start spark cluster
     *
     * @param sparkCluster SparkCluster to be start
     */
    void start(SparkCluster sparkCluster);

    /**
     * Shutdown spark cluster
     *
     * @param sparkCluster to be shutdown
     */
    void shutdown(SparkCluster sparkCluster);

    /**
     * Allow to shut down spark cluster
     *
     * @param sparkCluster SparkCluster can be shutdown now
     * @return Whether the operation was successful
     */
    Boolean allowShutdownCluster(SparkCluster sparkCluster);

    /**
     * Query whether the Flink cluster with the specified cluster id exists
     *
     * @param clusterId target cluster id
     * @param id Current spark cluster id
     * @return Whether the cluster exists
     */
    Boolean existsByClusterId(String clusterId, Long id);

    /**
     * Query whether the Flink cluster with the specified cluster id exists
     *
     * @param clusterName target cluster name
     * @return Whether the cluster exists
     */
    Boolean existsByClusterName(String clusterName);

    /**
     * Query whether the Flink cluster with the specified FlinkEnv id exists
     *
     * @param id FlinkEnv id
     * @return Whether the cluster exists
     */
    Boolean existsBySparkEnvId(Long id);

    /**
     * Lists the corresponding spark clusters based on DeployMode
     *
     * @param deployModeEnums Collection of FlinkDeployMode
     * @return List of spark cluster
     */
    List<SparkCluster> listByDeployModes(Collection<SparkDeployMode> deployModeEnums);

    /**
     * update spark cluster state
     *
     * @param id spark cluster id
     * @param state spark cluster state
     */
    void updateClusterState(Long id, ClusterState state);

    IPage<SparkCluster> findPage(SparkCluster sparkCluster, RestRequest restRequest);

}
