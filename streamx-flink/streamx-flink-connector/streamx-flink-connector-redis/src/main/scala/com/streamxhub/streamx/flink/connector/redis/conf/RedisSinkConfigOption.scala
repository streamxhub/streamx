/*
 * Copyright (c) 2019 The StreamX Project
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.streamxhub.streamx.flink.connector.redis.conf


import com.streamxhub.streamx.common.conf.ConfigOption

import java.util.Properties

/**
 * @author benjobs
 */
object RedisSinkConfigOption {

  /**
   *
   * @param properties
   * @return
   */
  def apply(properties: Properties = new Properties): RedisSinkConfigOption = new RedisSinkConfigOption(properties)

}

class RedisSinkConfigOption(properties: Properties) {

  implicit val (prefix, prop) = ("redis.sink", properties)

  val DEFAULT_CONNECT_TYPE: String = "jedisPool"

  val connectType = ConfigOption(
    key = "connectType",
    required = false,
    defaultValue = DEFAULT_CONNECT_TYPE,
    classType = classOf[String],
    handle = k => {
      val value: String = properties
        .remove(k).toString
      if (value == null && value.isEmpty) DEFAULT_CONNECT_TYPE else value
    }

  )

}
