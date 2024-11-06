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

package org.apache.streampark.console.core.entity;

import org.apache.streampark.common.enums.SparkDeployMode;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@TableName("t_spark_cluster")
@Getter
@Setter
public class SparkCluster implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String masterUrl;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String masterWebUrl;

    private String clusterId;

    private String clusterName;

    private String description;

    private Integer deployMode;

    /**
     * spark version
     */
    private Long versionId;

    private Integer resolveOrder;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String exception;

    private Integer clusterState;

    private Date createTime;

    private Date startTime;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date endTime;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long alertId;

    private transient Integer allJobs = 0;

    private transient Integer affectedJobs = 0;

    public SparkDeployMode getDeployModeEnum() {
        return SparkDeployMode.of(deployMode);
    }

}
