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

package org.apache.streampark.spark.kubernetes.model

import org.apache.streampark.common.util.Utils

import scala.util.Try

/** Pod template for Spark k8s cluster */
case class SparkK8sPodTemplates(
    driverPodTemplate: String = "",
    executorPodTemplate: String = "") {

  def nonEmpty: Boolean = Option(driverPodTemplate).exists(_.trim.nonEmpty) ||
    Option(executorPodTemplate).exists(_.trim.nonEmpty)

  def isEmpty: Boolean = !nonEmpty

  override def hashCode(): Int =
    Utils.hashCode(driverPodTemplate, executorPodTemplate)

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: SparkK8sPodTemplates =>
        Try(driverPodTemplate.trim).getOrElse("") == Try(that.driverPodTemplate.trim)
          .getOrElse("") &&
        Try(executorPodTemplate.trim).getOrElse("") == Try(that.executorPodTemplate.trim)
          .getOrElse("")
      case _ => false
    }
  }

}

object SparkK8sPodTemplates {

  def empty: SparkK8sPodTemplates = new SparkK8sPodTemplates()

  def of(driverPodTemplate: String, executorPodTemplate: String): SparkK8sPodTemplates =
    SparkK8sPodTemplates(safeGet(driverPodTemplate), safeGet(executorPodTemplate))

  private[this] def safeGet(content: String): String = {
    content match {
      case null => ""
      case x if x.trim.isEmpty => ""
      case x => x
    }
  }

}
