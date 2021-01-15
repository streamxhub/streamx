/*
 * Copyright (c) 2019 The StreamX Project
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

package com.streamxhub.plugin.profiling.profiler;

import com.streamxhub.plugin.profiling.util.ClassAndMethodLongMetricBuffer;

/** @author benjobs */
public class MethodDurationCollector {
  private ClassAndMethodLongMetricBuffer buffer;

  public MethodDurationCollector(ClassAndMethodLongMetricBuffer buffer) {
    this.buffer = buffer;
  }

  public void collectLongMetric(
      String className, String methodName, String metricName, long metricValue) {
    buffer.appendValue(className, methodName, metricName, metricValue);
  }
}
