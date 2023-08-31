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

package org.apache.streampark.console.core.service.application;

import org.apache.streampark.console.core.entity.Application;
import org.apache.streampark.console.core.enums.AppExistsState;

import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This interface defines the methods that can be used for various utility operations related to an
 * application.
 */
public interface ApplicationInfoService extends IService<Application> {
  /**
   * Checks if a record exists in the database with the given team ID.
   *
   * @param teamId The ID of the team to check.
   * @return true if a record with the given team ID exists, false otherwise.
   */
  boolean existsByTeamId(Long teamId);

  /**
   * Checks if a record exists in the database with the given user ID.
   *
   * @param userId The ID of the user to check.
   * @return true if a record with the given user ID exists, false otherwise.
   */
  boolean existsByUserId(Long userId);

  /**
   * Checks if there exists a running job for the given cluster ID.
   *
   * @param clusterId The ID of the cluster.
   * @return true if there is a running job for the given cluster ID, false otherwise.
   */
  boolean existsRunningJobByClusterId(Long clusterId);

  /**
   * Checks if a job exists for a given cluster ID.
   *
   * @param clusterId The ID of the cluster.
   * @return true if a job exists for the given cluster ID; otherwise, false.
   */
  boolean existsJobByClusterId(Long clusterId);

  /**
   * Checks if there is a job that is associated with the given Flink environment ID.
   *
   * @param flinkEnvId The ID of the Flink environment.
   * @return True if a job exists for the given Flink environment ID, false otherwise.
   */
  boolean existsJobByFlinkEnvId(Long flinkEnvId);

  /**
   * Counts the number of jobs belonging to a given cluster ID.
   *
   * @param clusterId The ID of the cluster.
   * @return The number of jobs belonging to the cluster.
   */
  Integer countJobsByClusterId(Long clusterId);

  /**
   * Counts the number of affected jobs by the given cluster ID and database type.
   *
   * @param clusterId The ID of the cluster.
   * @param dbType The type of the database.
   * @return The number of affected jobs.
   */
  Integer countAffectedJobsByClusterId(Long clusterId, String dbType);

  /**
   * Gets the YARN name for the given application.
   *
   * @param app The application for which to retrieve the YARN name.
   * @return The YARN name of the application as a String.
   */
  String getYarnName(Application app);

  /**
   * Checks if the given application exists in the system.
   *
   * @param app The application to check for existence.
   * @return AppExistsState indicating the existence state of the application.
   */
  AppExistsState checkExists(Application app);

  /**
   * Persists the metrics of the given application.
   *
   * @param application The application which metrics need to be persisted.
   */
  void persistMetrics(Application application);

  /**
   * Reads the configuration for the given application and returns it as a String.
   *
   * @param app The application for which the configuration needs to be read.
   * @return The configuration for the given application as a String.
   * @throws IOException If an I/O error occurs while reading the configuration.
   */
  String readConf(Application app) throws IOException;

  /**
   * Retrieves the main configuration value for the given Application.
   *
   * @param application the Application object for which to fetch the main configuration value
   * @return the main configuration value as a String
   */
  String getMain(Application application);

  /**
   * Returns the dashboard for the specified team.
   *
   * @param teamId the ID of the team
   * @return a map containing the dashboard data
   */
  Map<String, Serializable> dashboard(Long teamId);

  /**
   * Retrieves the Kubernetes start log for a specific ID with an optional offset and limit.
   *
   * @param id The ID of the Kubernetes resource.
   * @param offset The offset to start fetching log lines from.
   * @param limit The maximum number of log lines to fetch.
   * @return The Kubernetes start log as a string.
   * @throws Exception if an error occurs while retrieving the log.
   */
  String k8sStartLog(Long id, Integer offset, Integer limit) throws Exception;

  /** */
  List<String> getRecentK8sNamespace();

  /**
   * Retrieves the list of recent K8s cluster IDs based on the specified execution mode.
   *
   * @param executionMode The execution mode to filter the recent K8s cluster IDs. 1: Production
   *     mode 2: Test mode 3: Development mode -1: All modes
   * @return The list of recent K8s cluster IDs based on the specified execution mode.
   */
  List<String> getRecentK8sClusterId(Integer executionMode);

  /**
   * Retrieves the list of recent Flink base images.
   *
   * @return a list of strings representing the recent Flink base images
   */
  List<String> getRecentFlinkBaseImage();

  /**
   * Retrieves the recent K8s pod templates.
   *
   * @return a List of Strings representing the recent K8s pod templates.
   */
  List<String> getRecentK8sPodTemplate();

  /**
   * Retrieves the list of recent Kubernetes Job Manager Pod templates.
   *
   * @return A List of string values representing the recent Kubernetes Job Manager Pod templates.
   */
  List<String> getRecentK8sJmPodTemplate();

  /**
   * Retrieves the list of recent K8s TM pod templates.
   *
   * @return The list of recent K8s TM pod templates as a List of String objects.
   */
  List<String> getRecentK8sTmPodTemplate();

  /**
   * Uploads a list of jars to the server for historical reference.
   *
   * @return A list of strings representing the names of the uploaded jars.
   */
  List<String> historyUploadJars();
}
