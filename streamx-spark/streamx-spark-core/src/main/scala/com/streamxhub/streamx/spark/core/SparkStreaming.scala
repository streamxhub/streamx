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

package com.streamxhub.streamx.spark.core

import com.streamxhub.streamx.common.conf.ConfigConst._
import com.streamxhub.streamx.common.util.{PropertiesUtils, SystemPropertyUtils}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.annotation.meta.getter
import scala.collection.mutable.ArrayBuffer

/**
 * <b><code>SparkBatch</code></b>
 * <p/>
 * Spark 流处理 入口封装
 * <p/>
 * <b>Creation Time:</b> 2022/8/8 20:44.
 *
 * @author gn
 * @since streamx ${PROJECT_VERSION}
 */
trait SparkStreaming extends Spark {


  @(transient@getter)
  var context: StreamingContext = _

  /**
   * StreamingContext 运行之前执行
   *
   * @param ssc
   */
  def beforeStarted(): Unit = {}

  /**
   * StreamingContext 运行之后执行
   */
  def afterStarted(): Unit = {}

  /**
   * StreamingContext 停止后 程序停止前 执行
   */
  def beforeStop(): Unit = {}

  /**
   * 处理函数
   *
   * @param ssc
   */
  def handle(): Unit


  def main(args: Array[String]): Unit = {

    this._args = args

    initArgs(args)
    initialize(sparkConf)

    // 时间间隔
    val duration = sparkConf.get(KEY_SPARK_BATCH_DURATION).toInt


    def initContext() = {
      // 时间间隔
      val duration = sparkConf.get(KEY_SPARK_BATCH_DURATION).toInt
      this.context = new StreamingContext(sparkSession.sparkContext, Seconds(duration))
      handle()
      this.context
    }

    checkpoint match {
      case "" => initContext()
      case ck =>
        this.context = StreamingContext.getOrCreate(ck, initContext, createOnError = createOnError)
        this.context.checkpoint(ck)
    }

    beforeStarted()
    this.context.start()
    afterStarted()
    this.context.awaitTermination()
    beforeStop()
  }

}

