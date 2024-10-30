package org.apache.streampark.spark.kubernetes

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.streampark.spark.kubernetes.model.K8sPodTemplates

import java.io.File
import scala.collection.mutable

object PodTemplateTool {

  val KUBERNETES_DRIVER_POD_TEMPLATE: PodTemplateType =
    PodTemplateType("spark.kubernetes.driver.podTemplateFile", "driver-pod-template.yaml")

  val KUBERNETES_EXECUTOR_POD_TEMPLATE: PodTemplateType =
    PodTemplateType("spark.kubernetes.executor.podTemplateFile", "executor-pod-template.yaml")

  /**
   * Prepare kubernetes pod template file to buildWorkspace direactory.
   *
   * @param buildWorkspace
   *   project workspace dir of flink job
   * @param podTemplates
   *   flink kubernetes pod templates
   * @return
   *   Map[k8s pod template option, template file output path]
   */
  def preparePodTemplateFiles(
      buildWorkspace: String,
      podTemplates: K8sPodTemplates): K8sPodTemplateFiles = {
    val workspaceDir = new File(buildWorkspace)
    if (!workspaceDir.exists()) {
      workspaceDir.mkdir()
    }

    val podTempleMap = mutable.Map[String, String]()
    val outputTmplContent = (tmplContent: String, podTmpl: PodTemplateType) => {
      if (StringUtils.isNotBlank(tmplContent)) {
        val outputPath = s"$buildWorkspace/${podTmpl.fileName}"
        val outputFile = new File(outputPath)
        FileUtils.write(outputFile, tmplContent, "UTF-8")
        podTempleMap += (podTmpl.key -> outputPath)
      }
    }

    outputTmplContent(podTemplates.driverPodTemplate, KUBERNETES_DRIVER_POD_TEMPLATE)
    outputTmplContent(podTemplates.executorPodTemplate, KUBERNETES_EXECUTOR_POD_TEMPLATE)
    K8sPodTemplateFiles(podTempleMap.toMap)
  }

}

/**
 * @param tmplFiles
 *   key of flink pod template configuration -> absolute file path of pod template
 */
case class K8sPodTemplateFiles(tmplFiles: Map[String, String]) {

  /** merge k8s pod template configuration to Flink Configuration */
  def mergeToFlinkConf(flinkConf: Configuration): Unit =
    tmplFiles
      .filter(_._2.nonEmpty)
      .foreach(e => flinkConf.setString(e._1, e._2))

}

case class PodTemplateType(key: String, fileName: String)
