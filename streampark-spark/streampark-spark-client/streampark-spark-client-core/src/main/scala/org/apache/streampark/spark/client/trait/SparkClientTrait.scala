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

package org.apache.streampark.spark.client.`trait`

import org.apache.streampark.common.util._
import org.apache.streampark.common.util.Implicits._
import org.apache.streampark.spark.client.bean._

import org.apache.commons.lang3.StringUtils
import org.apache.spark.launcher.{SparkAppHandle, SparkLauncher}

import java.util.concurrent.CountDownLatch

import scala.util.{Failure, Success, Try}

trait SparkClientTrait extends Logger {

  @throws[Exception]
  def submit(submitRequest: SubmitRequest): SubmitResponse = {
    logInfo(
      s"""
         |--------------------------------------- spark job start -----------------------------------
         |    userSparkHome    : ${submitRequest.sparkVersion.sparkHome}
         |    sparkVersion     : ${submitRequest.sparkVersion.version}
         |    appName          : ${submitRequest.appName}
         |    jobType          : ${submitRequest.jobType.name()}
         |    deployMode       : ${submitRequest.deployMode.name()}
         |    applicationType  : ${submitRequest.applicationType.getName}
         |    appArgs          : ${submitRequest.appArgs}
         |    appConf          : ${submitRequest.appConf}
         |    properties       : ${submitRequest.appProperties.mkString(",")}
         |-------------------------------------------------------------------------------------------
         |""".stripMargin)

    prepareConfig(submitRequest)

    setConfig(submitRequest)

    Try(doSubmit(submitRequest)) match {
      case Success(resp) => resp
      case Failure(e) =>
        logError(
          s"spark job ${submitRequest.appName} start failed, " +
            s"deployMode: ${submitRequest.deployMode.getName}, " +
            s"detail: ${ExceptionUtils.stringifyException(e)}")
        throw e
    }
  }

  def setConfig(submitRequest: SubmitRequest): Unit

  @throws[Exception]
  def cancel(stopRequest: CancelRequest): CancelResponse = {
    logInfo(
      s"""
         |----------------------------------------- spark job cancel ----------------------------------
         |     userSparkHome     : ${stopRequest.sparkVersion.sparkHome}
         |     sparkVersion      : ${stopRequest.sparkVersion.version}
         |     appId             : ${stopRequest.appId}
         |-------------------------------------------------------------------------------------------
         |""".stripMargin)

    doCancel(stopRequest)
  }

  @throws[Exception]
  def doSubmit(submitRequest: SubmitRequest): SubmitResponse

  @throws[Exception]
  def doCancel(cancelRequest: CancelRequest): CancelResponse

  protected def launch(sparkLauncher: SparkLauncher): SparkAppHandle = {
    logger.info("[StreamPark][Spark] The spark job start submitting")
    val submitFinished: CountDownLatch = new CountDownLatch(1)
    val sparkAppHandle = sparkLauncher.startApplication(new SparkAppHandle.Listener() {
      override def infoChanged(sparkAppHandle: SparkAppHandle): Unit = {}
      override def stateChanged(handle: SparkAppHandle): Unit = {
        if (handle.getAppId != null) {
          logger.info(s"${handle.getAppId} stateChanged : ${handle.getState.toString}")
        } else {
          logger.info("stateChanged : {}", handle.getState.toString)
        }
        if (handle.getAppId != null && submitFinished.getCount != 0) {
          // Task submission succeeded
          submitFinished.countDown()
        }
        if (handle.getState.isFinal) {
          if (StringUtils.isNotBlank(handle.getAppId)) {
            removeHandle(handle.getAppId)
          }
          if (submitFinished.getCount != 0) {
            // Task submission failed
            submitFinished.countDown()
          }
          logger.info("Task is end, final state : {}", handle.getState.toString)
        }
      }
    })
    submitFinished.await()
    sparkAppHandle
  }
  protected def removeHandle(appId: String): Unit
  protected def setSparkConfig(submitRequest: SubmitRequest, sparkLauncher: SparkLauncher): Unit = {
    // 1) set spark conf
    submitRequest.appProperties.foreach(prop => {
      val k = prop._1
      val v = prop._2
      logInfo(s"| $k  : $v")
      sparkLauncher.setConf(k, v)
    })
    // 3) set spark args
    submitRequest.appArgs.foreach(sparkLauncher.addAppArgs(_))
    if (submitRequest.hasExtra("sql")) {
      sparkLauncher.addAppArgs("--sql", submitRequest.getExtra("sql").toString)
    }
  }

  private def prepareConfig(submitRequest: SubmitRequest): Unit = {
    // 1) filter illegal configuration key
    val userConfig = submitRequest.appProperties.filter(c => {
      val k = c._1
      if (k.startsWith("spark.")) {
        true
      } else {
        logger.warn("[StreamPark] config {} doesn't start with \"spark.\" Skip it.", k)
        false
      }
    })
    val defaultConfig = submitRequest.DEFAULT_SUBMIT_PARAM.filter(c => !userConfig.containsKey(c._1) && !submitRequest.sparkParameterMap.containsKey(c._1))
    submitRequest.appProperties.clear()
    // 2) put default configuration
    submitRequest.appProperties.putAll(defaultConfig)
    // 3) put configuration from template (spark-application.conf)
    submitRequest.appProperties.putAll(submitRequest.sparkParameterMap)
    // 4) put configuration from appProperties
    submitRequest.appProperties.putAll(userConfig)
  }

}
