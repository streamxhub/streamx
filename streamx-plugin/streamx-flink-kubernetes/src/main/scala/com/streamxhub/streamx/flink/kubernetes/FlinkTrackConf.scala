/*
 * Copyright (c) 2021 The StreamX Project
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.streamxhub.streamx.flink.kubernetes

/**
 * author: Al-assad
 *
 * @param jobStatusWatcherConf configuration for flink job status tracking process
 * @param metricWatcherConf    configuration for flink metric tracking process
 */
case class FlinkTrackConf(jobStatusWatcherConf: JobStatusWatcherConf, metricWatcherConf: MetricWatcherConf)

case class MetricWatcherConf(sglTrkTaskTimeoutSec: Long, sglTrkTaskIntervalSec: Long)

case class JobStatusWatcherConf(sglTrkTaskTimeoutSec: Long, sglTrkTaskIntervalSec: Long)


object FlinkTrackConf {
  def default: FlinkTrackConf = FlinkTrackConf(JobStatusWatcherConf.default, MetricWatcherConf.default)
}

object MetricWatcherConf {
  def default: MetricWatcherConf = MetricWatcherConf(sglTrkTaskTimeoutSec = 120, sglTrkTaskIntervalSec = 30)
}

object JobStatusWatcherConf {
  def default: JobStatusWatcherConf = JobStatusWatcherConf(sglTrkTaskTimeoutSec = 120, sglTrkTaskIntervalSec = 10)
}


