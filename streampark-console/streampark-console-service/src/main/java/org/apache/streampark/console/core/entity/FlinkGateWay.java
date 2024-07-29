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

import org.apache.streampark.console.base.mybatis.entity.BaseEntity;
import org.apache.streampark.console.core.enums.GatewayTypeEnum;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_flink_gateway")
public class FlinkGateWay extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "{required}")
    private String gatewayName;

    private String description;

    private GatewayTypeEnum gatewayType;

    @NotBlank(message = "{required}")
    private String address;

}
