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

import org.apache.streampark.common.constants.Constants
import org.apache.streampark.common.fs.LfsOperator

import org.apache.commons.io.FileUtils

import java.io.File
import java.nio.file.{Path, Paths}

/** Spark image dockerfile template. */
trait SparkDockerfileTemplateTrait {

  /** Path of dockerfile workspace, it should be a directory. */
  def workspacePath: String

  /** Spark base docker image name, see https://hub.docker.com/r/apache/spark */
  def sparkBaseImage: String

  /** Path of spark job main jar which would copy to $SPARK_HOME/usrlib/ */
  def sparkMainJarPath: String

  /** Path of additional spark lib path which would copy to $SPARK_HOME/lib/ */
  def sparkExtraLibPaths: Set[String]

  /** Offer content of DockerFile. */
  def offerDockerfileContent: String

  /** Startup spark main jar path inner Docker */
  def innerMainJarPath: String = s"local:///opt/spark/usrlib/$mainJarName"

  /** output dockerfile name */
  protected val DEFAULT_DOCKER_FILE_NAME = "Dockerfile"
  protected val SPARK_LIB_PATH = "lib"
  protected val SPARK_HOME: String = "$SPARK_HOME"

  /** Dockerfile building workspace. */
  lazy val workspace: Path = {
    val path = Paths.get(workspacePath).toAbsolutePath
    if (!LfsOperator.exists(workspacePath)) LfsOperator.mkdirs(workspacePath)
    path
  }

  /**
   * spark main jar name, the main jar would copy from `sparkMainjarPath` to
   * `workspacePath/mainJarName.jar`.
   */
  lazy val mainJarName: String = {
    val mainJarPath = Paths.get(sparkMainJarPath).toAbsolutePath
    if (mainJarPath.getParent != workspace) {
      LfsOperator.copy(
        mainJarPath.toString,
        s"${workspace.toString}/${mainJarPath.getFileName.toString}")
    }
    mainJarPath.getFileName.toString
  }

  /**
   * spark extra jar lib, the jar file in `sparkExtraLibPaths` would be copyed into
   * `SPARK_LIB_PATH`.
   */
  lazy val extraLibName: String = {
    LfsOperator.mkCleanDirs(s"${workspace.toString}/$SPARK_LIB_PATH")
    sparkExtraLibPaths
      .map(new File(_))
      .filter(_.exists())
      .filter(_.getName.endsWith(Constants.JAR_SUFFIX))
      .flatMap {
        case f if f.isDirectory =>
          f.listFiles
            .filter(_.isFile)
            .filter(_.getName.endsWith(Constants.JAR_SUFFIX))
            .map(_.getAbsolutePath)
        case f if f.isFile => Array(f.getAbsolutePath)
      }
      .foreach(LfsOperator.copy(_, s"${workspace.toString}/$SPARK_LIB_PATH"))
    SPARK_LIB_PATH
  }

  /**
   * write content of DockerFile to outputPath, the output dockerfile name is "dockerfile".
   *
   * @return
   *   File Object for actual output Dockerfile
   */
  def writeDockerfile: File = {
    val output = new File(s"$workspacePath/$DEFAULT_DOCKER_FILE_NAME")
    FileUtils.write(output, offerDockerfileContent, "UTF-8")
    output
  }

  /**
   * write content of DockerFile to outputPath using specified output dockerfile name.
   *
   * @return
   *   File Object for actual output Dockerfile
   */
  def writeDockerfile(dockerfileName: String): File = {
    val output = new File(s"$workspacePath/$dockerfileName")
    FileUtils.write(output, offerDockerfileContent, "UTF-8")
    output
  }

}
