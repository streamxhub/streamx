/*
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
export default {
  id: 'ID',
  appName: 'Application Name',
  searchName: 'Name',
  tags: 'Tags',
  owner: 'Owner',
  flinkVersion: 'Flink Version',
  duration: 'Duration',
  modifiedTime: 'Modified Time',
  runStatus: 'Run Status',
  launchBuild: 'Launch Status',
  jobType: 'Job Type',
  developmentMode: 'Development Mode',
  executionMode: 'Execution Mode',
  historyVersion: 'History Version',
  dependency: 'Dependency',
  appConf: 'Application Conf',
  resolveOrder: 'resolveOrder',
  parallelism: 'Parallelism',
  restartSize: 'Fault Restart Size',
  faultAlertTemplate: 'Fault Alert Template',
  checkPointFailureOptions: 'CheckPoint Failure Options',
  totalMemoryOptions: 'Total Memory Options',
  jmMemoryOptions: 'JM Memory Options',
  tmMemoryOptions: 'TM Memory Options',
  podTemplate: 'Kubernetes Pod Template',
  flinkCluster: 'Flink Cluster',
  yarnQueue: 'Yarn Queue',
  mavenPom: 'Maven pom',
  uploadJar: 'Upload Jar',
  kubernetesNamespace: 'Kubernetes Namespace',
  kubernetesClusterId: 'Kubernetes ClusterId',
  flinkBaseDockerImage: 'Flink Base Docker Image',
  restServiceExposedType: 'Rest-Service Exposed Type',
  resourceFrom: 'Resource From',
  uploadJobJar: 'Upload Job Jar',
  mainClass: 'Program Main',
  project: 'Project',
  module: 'Module',
  appType: 'Application Type',
  programArgs: 'Program Args',
  programJar: 'Program Jar',
  dynamicProperties: 'Dynamic Properties',
  hadoopConfigTitle: 'System Hadoop Configuration',
  dragUploadTitle: 'Click or drag jar to this area to upload',
  dragUploadTip:
    'Support for a single upload. You can upload a local jar here to support for current Job',
  dependencyError: 'please set flink version first.',
  status: 'Run Status',
  startTime: 'Start Time',
  endTime: 'End Time',
  launch: {
    launchTitle: 'The current launch of the application is in progress.',
    launchDesc: 'are you sure you want to force another build',
    launchFail: 'launch application failed,',
    launching: 'Current Application is launching',
  },
  detail: {
    detailTitle: 'Application Info',
    flinkWebUi: 'Flink Web UI',
    compareConfig: 'Compare Config',
    candidate: 'Candidate',
    compare: 'Compare',
    resetApi: 'Rest Api',
    resetApiToolTip:
      'Rest API external call interface,other third-party systems easy to access StreamPark',
    copyStartcURL: 'Copy Start cURL',
    copyCancelcURL: 'Copy Cancel cURL',
    apiDocCenter: 'Api Doc Center',
    nullAccessToken: 'access token is null,please contact the administrator to add.',
    invalidAccessToken: 'access token is invalid,please contact the administrator.',
    detailTab: {
      configDetail: 'View Config Detail',
      confDeleteTitle: 'Are you sure delete this record',
      copyPath: 'Copy Path',
      pointDeleteTitle: 'Are you sure delete?',
      copySuccess: 'copied to clipboard successfully',
      copyFail: 'failed',
      check: 'Check Point',
      save: 'Save Point',
      exception: 'View Exception',
    },
    different: {
      original: 'Original version',
      target: 'Target version',
    },
    exceptionModal: {
      title: 'Exception Info',
    },
  },
  view: {
    buildTitle: 'Application Launching Progress',
    stepTitle: 'Steps Detail',
    errorLog: 'Error Log',
    errorSummary: 'Error Summary',
    errorStack: 'Error Stack',
    logTitle: 'Start Log : Application Name [ {0} ]',
    refreshTime: 'last refresh time',
    refresh: 'refresh',
    start: 'Start Application',
    stop: 'Stop application',
    recheck: 'the associated project has changed and this job need to be rechecked',
    changed: 'the application has changed.',
  },
  pod: {
    choice: 'Choice',
    init: 'Init Content',
    host: 'Host Alias',
  },
  flinkSql: {
    preview: 'Preview',
    verify: 'Verify',
    format: 'Format',
    fullScreen: 'Full Screen',
    exit: 'Exit',
    successful: 'Verification success',
    compareFlinkSQL: 'Compare Flink SQL',
  },
  editStreamPark: {
    success: 'update successful',
    flinkSqlRequired: 'Flink Sql is required',
    appidCheck: 'appid can not be empty',
    sqlCheck: 'SQL check error',
  },
  operation: {
    edit: 'Edit Application',
    launch: 'Launch Application',
    launchDetail: 'Launching Progress Detail',
    start: 'Start Application',
    cancel: 'Cancel Application',
    detail: 'View Application Detail',
    startLog: 'See Flink Start log',
    force: 'Forced Stop Application',
    copy: 'Copy Application',
    remapping: 'Remapping Application',
    flameGraph: 'View FlameGraph',
    deleteTip: 'Are you sure delete this job ?',
  },
  dashboard: {
    availableTaskSlots: 'Available Task Slots',
    taskSlots: 'Task Slots',
    taskManagers: 'Task Managers',
    runningJobs: 'Running Jobs',
    totalTask: 'Total Task',
    runningTask: 'Running Task',
    jobManagerMemory: 'JobManager Memory',
    totalJobManagerMemory: 'Total JobManager Mem',
    taskManagerMemory: 'TaskManager Memory',
    totalTaskManagerMemory: 'Total TaskManager Mem',
  },
  runStatusOptions: {
    added: 'ADDED',
    starting: 'STARTING',
    running: 'RUNNING',
    failed: 'FAILED',
    canceled: 'CANCELED',
    finished: 'FINISHED',
    suspended: 'SUSPENDED',
    lost: 'LOST',
    silent: 'SILENT',
    terminated: 'TERMINATED',
  },
  addAppTips: {
    developmentModePlaceholder: 'please select job type',
    developmentModeIsRequiredMessage: 'Job Type is required',
    executionModePlaceholder: 'please select execution mode',
    executionModeIsRequiredMessage: 'Execution Mode is required',
    hadoopEnvInitMessage:
      'Hadoop environment initialization failed, please check the environment settings',
    resourceFromMessage: 'resource from is required',
    mainClassPlaceholder: 'Please enter Main class',
    mainClassIsRequiredMessage: 'Program Main is required',
    projectPlaceholder: 'Please select Project',
    projectIsRequiredMessage: 'Project is required',
    projectModulePlaceholder: 'Please select module of this project',
    appTypePlaceholder: 'Please select Application type',
    appTypeIsRequiredMessage: 'Application Type is required',
    programJarIsRequiredMessage: 'Program Jar is required',
    useSysHadoopConf: 'Use System Hadoop Conf',
    flinkVersionIsRequiredMessage: 'Flink Version is required',
    appNamePlaceholder: 'Please enter application Name',
    appNameIsRequiredMessage: 'The application Name is required',
    appNameNotUniqueMessage: 'The application Name is already exists, must be unique. ',
    appNameExistsInYarnMessage:
      'The application name is already exists in YARN, cannot be repeated. Please check',
    appNameExistsInK8sMessage:
      'The application name is already exists in Kubernetes,cannot be repeated. Please check',
    appNameNotValid:
      'The application name is invalid.characters must be (Chinese|English|"-"|"_"),two consecutive spaces cannot appear.Please check',
    flinkClusterIsRequiredMessage: 'Flink Cluster is required',
    flinkSqlIsRequiredMessage: 'Flink SQL is required',
    tagsPlaceholder: 'Please enter tags,if more than one, separate them with commas(,)',
    parallelismPlaceholder: 'The parallelism with which to run the program',
    slotsOfPerTaskManagerPlaceholder: 'Number of slots per TaskManager',
    restartSizePlaceholder: 'restart max size',
    alertTemplatePlaceholder: 'Alert Template',
    totalMemoryOptionsPlaceholder: 'Please select the resource parameters to set',
    tmPlaceholder: 'Please select the resource parameters to set',
    yarnQueuePlaceholder: 'Please enter yarn queue',
    descriptionPlaceholder: 'Please enter description for this application',
    kubernetesNamespacePlaceholder: 'Please enter kubernetes Namespace, e.g: default',
    kubernetesClusterIdPlaceholder: 'Please enter Kubernetes clusterId',
    kubernetesClusterIdRequire:
      "lower case alphanumeric characters, '-' or '.', and must start and end with an alphanumeric character",
    kubernetesClusterIdIsRequiredMessage: 'Kubernetes clusterId is required',
    flinkImagePlaceholder:
      'Please enter the tag of Flink base docker image, such as: flink:1.13.0-scala_2.11-java8',
    flinkImageIsRequiredMessage: 'Flink Base Docker Image is required',
    k8sRestExposedTypePlaceholder: 'kubernetes.rest-service.exposed.type',
    hadoopXmlConfigFileTips:
      'Automatically copy configuration files from system environment parameters',
    dynamicPropertiesPlaceholder:
      '$key=$value,If there are multiple parameters,you can new line enter them (-D <arg>)',
  },
  noteInfo: {
    note: 'Note',
    minute: 'minute',
    count: 'count',
    officialDoc: 'official document',
    checkPointFailureOptions: 'CheckPoint Failure Options',
    checkpointFailureRateInterval: 'checkpoint failure rate interval',
    maxFailuresPerInterval: 'max failures per interval',
    checkPointFailureNote:
      'Operation after checkpoint failure, e.g: Within 5 minutes (checkpoint failure rate interval), if the number of checkpoint failures reaches 10 (max failures per interval),action will be triggered(alert or restart job)',
    totalMemoryNote:
      'Explicitly configuring both "total process memory" and "total Flink memory" is not recommended. It may lead to deployment failures due to potential memory configuration conflicts. Configuring other memory components also requires caution as it can produce further configuration conflicts, The easiest way is to set "total process memory"',
    dynamicProperties:
      'It works the same as -D$property=$value in CLI mode, Allows specifying multiple generic configuration options. The available options can be found from',
  },
};
