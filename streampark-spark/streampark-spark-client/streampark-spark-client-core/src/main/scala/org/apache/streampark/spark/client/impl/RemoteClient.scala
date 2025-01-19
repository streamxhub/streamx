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

package org.apache.streampark.spark.client.impl

import org.apache.streampark.common.conf.ConfigKeys.{MASTER_URl, MASTER_WEB_URl}
import org.apache.streampark.common.util.Implicits._
import org.apache.streampark.spark.client.`trait`.SparkClientTrait
import org.apache.streampark.spark.client.bean.{CancelRequest, CancelResponse, SubmitRequest, SubmitResponse}

import org.apache.commons.lang3.StringUtils
import org.apache.spark.launcher.{SparkAppHandle, SparkLauncher}

import java.util.concurrent.ConcurrentHashMap

import scala.util.{Failure, Success, Try}

/** Remote application mode submit */
object RemoteClient extends SparkClientTrait {

  private lazy val sparkHandles = new ConcurrentHashMap[String, SparkAppHandle]()

  override def doCancel(cancelRequest: CancelRequest): CancelResponse = {
    // spark not need cancel
    val sparkAppHandle = sparkHandles.remove(cancelRequest.appId)
    if (sparkAppHandle != null) {
      Try(sparkAppHandle.stop()) match {
        case Success(_) =>
          logger.info(s"[StreamPark][Spark][RemoteClient] spark job: ${cancelRequest.appId} is stopped successfully.")
          CancelResponse(null)
        case Failure(e) =>
          logger.error("[StreamPark][Spark][RemoteClient] sparkAppHandle kill failed.", e)
          CancelResponse(null)
      }
    }
    CancelResponse(null)
  }

  override def setConfig(submitRequest: SubmitRequest): Unit = {}

  override def doSubmit(submitRequest: SubmitRequest): SubmitResponse = {
    // prepare spark launcher
    val launcher: SparkLauncher = prepareSparkLauncher(submitRequest)

    // 2) set Spark  config
    setSparkConfig(submitRequest, launcher)

    // start job
    Try(launch(launcher)) match {
      case Success(handle: SparkAppHandle) =>
        if (handle.getError.isPresent) {
          logger.info(s"[StreamPark][Spark][LocalClient] spark job: ${submitRequest.appName} submit failed.")
          throw handle.getError.get()
        } else {
          logger.info(s"[StreamPark][Spark][LocalClient] spark job: ${submitRequest.appName} submit successfully, " +
            s"appid: ${handle.getAppId}, " +
            s"state: ${handle.getState}")
          sparkHandles += handle.getAppId -> handle
          val masterWebUrl: String = submitRequest.getExtra(MASTER_WEB_URl).toString
          require(StringUtils.isNotBlank(masterWebUrl), "masterWebUrl is required.")
          val trackingUrl = s"${masterWebUrl}/app/?appId=${handle.getAppId}"
          SubmitResponse(handle.getAppId, trackingUrl, submitRequest.appProperties)
        }
      case Failure(e) => throw e
    }
  }

  private def prepareSparkLauncher(submitRequest: SubmitRequest) = {
    val env = new JavaHashMap[String, String]()
    val master: String = submitRequest.getExtra(MASTER_URl).toString
    require(StringUtils.isNotBlank(master), "master is required.")
    new SparkLauncher(env)
      .setSparkHome(submitRequest.sparkVersion.sparkHome)
      .setAppResource(submitRequest.userJarPath)
      .setMainClass(submitRequest.appMain)
      .setAppName(submitRequest.appName)
      .setVerbose(true)
      .setMaster(master)
  }

  override def removeHandle(appId: String): Unit = {
    sparkHandles.remove(appId)
  }
}
