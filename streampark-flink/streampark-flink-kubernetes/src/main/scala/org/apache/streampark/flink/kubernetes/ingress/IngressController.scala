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

package org.apache.streampark.flink.kubernetes.ingress

import org.apache.streampark.common.util.Logger

import io.fabric8.kubernetes.client.DefaultKubernetesClient
import org.apache.flink.client.program.ClusterClient

import scala.language.postfixOps

object IngressController extends Logger {

  val ingressStrategy: IngressStrategy = {
    if (getKubernetesVersion() >= 1.19) new IngressStrategyV1()
    else new IngressStrategyV1beta1()
  }

  def configureIngress(domainName: String, clusterId: String, nameSpace: String): Unit = {
    ingressStrategy.configureIngress(domainName, clusterId, nameSpace)
  }

  def ingressUrlAddress(
      nameSpace: String,
      clusterId: String,
      clusterClient: ClusterClient[_]): String = {
    ingressStrategy.ingressUrlAddress(nameSpace, clusterId, clusterClient)
  }

  def prepareIngressTemplateFiles(buildWorkspace: String, ingressTemplates: String): String = {
    ingressStrategy.prepareIngressTemplateFiles(buildWorkspace, ingressTemplates)
  }

  def getKubernetesVersion(): Double = {
    val client = new DefaultKubernetesClient()
    val versionInfo = client.getVersion
    val version = versionInfo.getMajor.toDouble + versionInfo.getMinor.toDouble / 10
    version
  }
}
