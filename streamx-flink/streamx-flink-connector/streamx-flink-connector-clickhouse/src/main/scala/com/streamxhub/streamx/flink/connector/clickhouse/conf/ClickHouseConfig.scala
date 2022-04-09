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

package com.streamxhub.streamx.flink.connector.clickhouse.conf

import com.streamxhub.streamx.flink.connector.failover.ThresholdConf

import java.util.{Base64, Properties}
import java.util.concurrent.ThreadLocalRandom
import scala.collection.JavaConversions._

/**
 *
 * Flink sink for Clickhouse database. Powered by Async Http Client.
 *
 * High-performance library for loading data to Clickhouse.
 *
 * It has two triggers for loading data: by timeout and by buffer size.
 *
 */
//---------------------------------------------------------------------------------------

class ClickHouseConfig(parameters: Properties) extends ThresholdConf(parameters) {

  val sinkOption: ClickHouseSinkConfigOption = ClickHouseSinkConfigOption(parameters)

  val user: String = sinkOption.user.get()

  val password: String = sinkOption.password.get()

  val hosts: List[String] = sinkOption.hosts.get()

  val table: String = sinkOption.targetTable.get()

  var currentHostId: Int = 0

  val credentials: String = (user, password) match {
    case (null, null) => null
    case (u, p) => new String(Base64.getEncoder.encode(s"$u:$p".getBytes))
  }

  def getRandomHostUrl: String = {
    currentHostId = ThreadLocalRandom.current.nextInt(hosts.size)
    hosts.get(currentHostId)
  }

  def nextHost: String = {
    if (currentHostId >= hosts.size - 1) {
      currentHostId = 0
    } else {
      currentHostId += 1
    }
    hosts.get(currentHostId)
  }

  println(s"user: $user, password:$password, hosts:$hosts, table:$table ")

}
