package org.apache.streampark.spark.kubernetes.model

import org.apache.streampark.common.util.Utils

import scala.util.Try

/** Pod template for flink k8s cluster */
case class K8sPodTemplates(
    driverPodTemplate: String = "",
    executorPodTemplate: String = "") {

  def nonEmpty: Boolean = Option(driverPodTemplate).exists(_.trim.nonEmpty) ||
    Option(executorPodTemplate).exists(_.trim.nonEmpty)

  def isEmpty: Boolean = !nonEmpty

  override def hashCode(): Int =
    Utils.hashCode(driverPodTemplate, executorPodTemplate)

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: K8sPodTemplates =>
        Try(driverPodTemplate.trim).getOrElse("") == Try(that.driverPodTemplate.trim)
          .getOrElse("") &&
        Try(executorPodTemplate.trim).getOrElse("") == Try(that.executorPodTemplate.trim)
          .getOrElse("")
      case _ => false
    }
  }

}

object K8sPodTemplates {

  def empty: K8sPodTemplates = new K8sPodTemplates()

  def of(driverPodTemplate: String, executorPodTemplate: String): K8sPodTemplates =
    K8sPodTemplates(safeGet(driverPodTemplate), safeGet(executorPodTemplate))

  private[this] def safeGet(content: String): String = {
    content match {
      case null => ""
      case x if x.trim.isEmpty => ""
      case x => x
    }
  }

}
