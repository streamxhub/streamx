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

package org.apache.streampark.flink.packer.docker

/**
 * Base spark docker file image template.
 *
 * @param workspacePath
 *   Path of dockerfile workspace, it should be a directory.
 * @param sparkBaseImage
 *   Spark base docker image name, see https://hub.docker.com/r/apache/spark.
 * @param sparkMainJarPath
 *   Path of spark job main jar which would copy to $SPARK_HOME/usrlib/
 * @param sparkExtraLibPaths
 *   Path of additional spark lib path which would copy to $SPARK_HOME/lib/
 */
case class SparkDockerfileTemplate(
    workspacePath: String,
    sparkBaseImage: String,
    sparkMainJarPath: String,
    sparkExtraLibPaths: Set[String])
  extends SparkDockerfileTemplateTrait {

  /** offer content of DockerFile */
  override def offerDockerfileContent: String = {
    s"""FROM $sparkBaseImage
       |USER root
       |RUN mkdir -p $SPARK_HOME/usrlib
       |COPY $mainJarName $SPARK_HOME/usrlib/$mainJarName
       |COPY $extraLibName $SPARK_HOME/lib/
       |""".stripMargin
  }
}
