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

import org.apache.streampark.common.enums.ClusterState;
import org.apache.streampark.common.enums.SparkDeployMode;
import org.apache.streampark.console.base.domain.RestRequest;
import org.apache.streampark.console.base.exception.ApiAlertException;
import org.apache.streampark.console.base.mybatis.pager.MybatisPager;
import org.apache.streampark.console.core.bean.ResponseResult;
import org.apache.streampark.console.core.entity.SparkApplication;
import org.apache.streampark.console.core.entity.SparkCluster;
import org.apache.streampark.console.core.mapper.SparkClusterMapper;
import org.apache.streampark.console.core.service.SparkClusterService;
import org.apache.streampark.console.core.service.SparkEnvService;
import org.apache.streampark.console.core.service.application.SparkApplicationActionService;
import org.apache.streampark.console.core.watcher.SparkClusterWatcher;

import org.apache.commons.lang3.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class SparkClusterServiceImpl extends ServiceImpl<SparkClusterMapper, SparkCluster>
    implements
        SparkClusterService {

    @Autowired
    private SparkClusterWatcher sparkClusterWatcher;

    @Autowired
    private SparkEnvService sparkEnvService;

    @Autowired
    SparkApplicationActionService sparkApplicationActionService;

    @Override
    public List<SparkCluster> listAvailableCluster() {
        return this.lambdaQuery().eq(SparkCluster::getClusterState, ClusterState.RUNNING.getState()).list();
    }

    @Override
    public ResponseResult check(SparkCluster sparkCluster) {
        ResponseResult result = new ResponseResult();
        result.setStatus(0);
        if (this.existsByClusterName(sparkCluster.getClusterName())) {
            result.setMsg("cluster name already exists, please check.");
            result.setStatus(1);
            return result;
        }
        if (this.existsByClusterId(sparkCluster.getClusterId(), sparkCluster.getId())) {
            result.setMsg("cluster id already exists, please check.");
            result.setStatus(2);
            return result;
        }
        if (StringUtils.isEmpty(sparkCluster.getMasterWebUrl())) {
            result.setMsg("The master web url address cannot be empty, please check.");
            result.setStatus(3);
            return result;
        }
        if (SparkDeployMode.isRemoteMode(sparkCluster.getDeployModeEnum())
            && !sparkClusterWatcher.verifyClusterConnection(sparkCluster)) {
            result.setMsg("The remote cluster connection failed, please check!");
            result.setStatus(4);
            return result;
        }
        return result;
    }

    @Override
    public Boolean create(SparkCluster sparkCluster, Long userId) {
        ApiAlertException.throwIfTrue(this.existsByClusterId(sparkCluster.getClusterId(), null),
            "cluster id already exists, please check.");
        ApiAlertException.throwIfTrue(this.existsByClusterName(sparkCluster.getClusterName()),
            "cluster name already exists, please check.");
        ApiAlertException.throwIfTrue(sparkEnvService.getById(sparkCluster.getVersionId()) == null,
            "spark env not exists, please check.");
        if (sparkClusterWatcher.verifyClusterConnection(sparkCluster)) {
            sparkCluster.setClusterState(ClusterState.RUNNING.getState());
            String masterUrl = sparkClusterWatcher.getSparkApplicationMetrics(sparkCluster.getMasterWebUrl()).url;
            sparkCluster.setMasterUrl(masterUrl);
        } else {
            sparkCluster.setClusterState(ClusterState.CREATED.getState());
        }
        sparkCluster.setCreateTime(new Date());
        sparkCluster.setStartTime(new Date());
        boolean ret = this.save(sparkCluster);
        if (ret && SparkDeployMode.isRemoteMode(sparkCluster.getDeployModeEnum())) {
            sparkClusterWatcher.addWatching(sparkCluster);
        }
        return ret;
    }

    @Override
    public void remove(Long id) {
        SparkCluster sparkCluster = this.getById(id);
        ApiAlertException.throwIfNull(sparkCluster, "spark cluster not exist, please check.");
        List<SparkApplication> linkApp = sparkApplicationActionService.getByClusterId(sparkCluster.getClusterId());
        ApiAlertException.throwIfTrue(
            linkApp != null && !linkApp.isEmpty(),
            "Some app on this cluster, the cluster cannot be delete, please check.");
        this.removeById(id);
    }

    @Override
    public void update(SparkCluster sparkCluster) {
        this.updateById(sparkCluster);
    }

    @Override
    public void start(SparkCluster sparkCluster) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void shutdown(SparkCluster sparkCluster) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean allowShutdownCluster(SparkCluster sparkCluster) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean existsByClusterId(String clusterId, Long id) {
        return this.lambdaQuery().ne(SparkCluster::getId, id).ne(id != null, SparkCluster::getClusterId, id).exists();
    }

    @Override
    public Boolean existsByClusterName(String clusterName) {
        return this.lambdaQuery().eq(SparkCluster::getClusterName, clusterName).exists();
    }

    @Override
    public Boolean existsBySparkEnvId(Long id) {
        return this.lambdaQuery().eq(SparkCluster::getVersionId, id).exists();
    }

    @Override
    public List<SparkCluster> listByDeployModes(Collection<SparkDeployMode> deployModeEnums) {
        return this.lambdaQuery().in(SparkCluster::getDeployModeEnum, deployModeEnums).list();
    }

    @Override
    public void updateClusterState(Long id, ClusterState state) {
        SparkCluster sparkCluster = new SparkCluster();
        sparkCluster.setId(id);
        sparkCluster.setClusterState(state.getState());
        this.updateById(sparkCluster);
    }

    @Override
    public IPage<SparkCluster> findPage(SparkCluster sparkCluster, RestRequest restRequest) {
        Page<SparkCluster> page = MybatisPager.getPage(restRequest);
        LambdaQueryWrapper<SparkCluster> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (sparkCluster != null) {
            lambdaQueryWrapper.like(StringUtils.isNotEmpty(sparkCluster.getClusterName()), SparkCluster::getClusterName,
                sparkCluster.getClusterName());
        }
        return this.baseMapper.selectPage(page, lambdaQueryWrapper);
    }

}
