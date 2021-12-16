/*
 * Copyright (c) 2021 The StreamX Project
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

package com.streamxhub.streamx.flink.packer.pipeline

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.annotation.Nullable

/**
 * Error details of building pipeline.
 *
 * @param summary   summary of error
 * @param exception exception stack
 * @author Al-assad
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = Array("exception"))
case class PipeErr(summary: String,
                   @Nullable exception: Throwable,
                   @Nullable exceptionStack: String) {

  def nonEmpty: Boolean = Option(summary).exists(_.nonEmpty) || exception != null

  def isEmpty: Boolean = !nonEmpty
}

object PipeErr {

  def empty(): PipeErr = of("", null)

  def of(summary: String, @Nullable exception: Throwable): PipeErr =
    PipeErr(summary, exception, if (exception == null) "" else exception.getStackTrace.mkString("\n"))
}


