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

package org.apache.streampark.console.core.controller;

import org.apache.streampark.common.enums.ClusterState;
import org.apache.streampark.console.base.domain.RestRequest;
import org.apache.streampark.console.base.domain.RestResponse;
import org.apache.streampark.console.base.exception.InternalException;
import org.apache.streampark.console.core.bean.ResponseResult;
import org.apache.streampark.console.core.entity.SparkCluster;
import org.apache.streampark.console.core.service.SparkClusterService;
import org.apache.streampark.console.core.util.ServiceHelper;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("spark/cluster")
public class SparkClusterController {

    @Autowired
    private SparkClusterService sparkClusterService;

    @PostMapping("page")
    public RestResponse findPage(SparkCluster sparkCluster, RestRequest restRequest) {
        IPage<SparkCluster> sparkClusters = sparkClusterService.findPage(sparkCluster, restRequest);
        return RestResponse.success(sparkClusters);
    }

    @PostMapping("alive")
    public RestResponse listAvailableCluster() {
        List<SparkCluster> sparkClusters = sparkClusterService.listAvailableCluster();
        return RestResponse.success(sparkClusters);
    }

    @PostMapping("list")
    public RestResponse list() {
        List<SparkCluster> sparkClusters = sparkClusterService.list();
        return RestResponse.success(sparkClusters);
    }

    @PostMapping("remote_url")
    public RestResponse remoteUrl(Long id) {
        SparkCluster cluster = sparkClusterService.getById(id);
        return RestResponse.success(cluster.getMasterUrl());
    }

    @PostMapping("check")
    public RestResponse check(SparkCluster cluster) {
        ResponseResult checkResult = sparkClusterService.check(cluster);
        return RestResponse.success(checkResult);
    }

    @PostMapping("create")
    @RequiresPermissions("cluster:create")
    public RestResponse create(SparkCluster cluster) {
        Long userId = ServiceHelper.getUserId();
        Boolean success = sparkClusterService.create(cluster, userId);
        return RestResponse.success(success);
    }

    @PostMapping("update")
    @RequiresPermissions("cluster:update")
    public RestResponse update(SparkCluster cluster) {
        sparkClusterService.update(cluster);
        return RestResponse.success();
    }

    @PostMapping("get")
    public RestResponse get(Long id) throws InternalException {
        SparkCluster cluster = sparkClusterService.getById(id);
        return RestResponse.success(cluster);
    }

    @PostMapping("start")
    public RestResponse start(SparkCluster cluster) {
        sparkClusterService.updateClusterState(cluster.getId(), ClusterState.STARTING);
        sparkClusterService.start(cluster);
        return RestResponse.success();
    }

    @PostMapping("shutdown")
    public RestResponse shutdown(SparkCluster cluster) {
        if (sparkClusterService.allowShutdownCluster(cluster)) {
            sparkClusterService.updateClusterState(cluster.getId(), ClusterState.CANCELLING);
            sparkClusterService.shutdown(cluster);
        }
        return RestResponse.success();
    }

    @PostMapping("delete")
    public RestResponse delete(SparkCluster cluster) {
        sparkClusterService.remove(cluster.getId());
        return RestResponse.success();
    }
}
