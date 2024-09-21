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
  dashboard: {
    runningTasks: 'Running Jobs',
    totalTask: 'Total Tasks',
    totalStage: 'Total Stages',
    completedTask: 'Total Completed Tasks',
    completedStage: 'Total Completed Stages',
    memory: 'Total Memory Used',
    VCore: 'Total VCores Used',
  },
  runState: {
    added: 'Added',
    new: 'New',
    saving: 'Saving',
    starting: 'Starting',
    submitted: 'Submitted',
    accept: 'Accepted',
    running: 'Running',
    finished: 'Finished',
    failed: 'Job Failed',
    lost: 'Job Lost',
    mapping: 'Mapping',
    other: 'Other',
    revoked: 'Revoked',
    stopping: 'Stopping',
    success: 'Succeeded',
    killed: 'Killed',
  },
  releaseState: {
    releasing: 'Releasing',
    failed: 'Release Failed',
    success: 'Release Successful',
    waiting: 'Waiting to Release',
    pending: 'Pending Rollback',
  },
  id: 'Applications ID',
  appName: 'Job Name',
  searchName: 'Job Name',
  tags: 'Tags',
  owner: 'Creator',
  sparkVersion: 'Spark Version',
  duration: 'Runtime',
  modifiedTime: 'Modification Time',
  runStatus: 'Run Status',
  releaseBuild: 'Release Status',
  jobType: 'Job Type',
  developmentMode: 'development Mode',
  executionMode: 'Execution Mode',
  historyVersion: 'History Version',
  resource: 'Spark App',
  resourcePlaceHolder: 'Select Resource',
  selectAppPlaceHolder: 'Select Applications',
  dependency: 'Job Dependency',
  appConf: 'Applications Configuration',
  resolveOrder: 'Class Loading Order',
  parallelism: 'Parallelism',
  restartSize: '(On Failure) Restart Count',
  faultAlertTemplate: 'Alert Template',
  checkPointFailureOptions: 'Checkpoint Alert Strategy',
  totalMemoryOptions: 'Total Memory',
  jmMemoryOptions: 'JM Memory',
  tmMemoryOptions: 'TM Memory',
  podTemplate: 'Kubernetes Pod Template',
  flinkCluster: 'Flink Cluster',
  yarnQueue: 'Yarn Queue',
  mavenPom: 'Maven POM',
  uploadJar: 'Upload Dependency Jar File',
  kubernetesNamespace: 'Kubernetes Namespace',
  kubernetesClusterId: 'Kubernetes Cluster ID',
  flinkBaseDockerImage: 'Flink Base Docker Image',
  restServiceExposedType: 'Rest-Service Exposed Type',
  resourceFrom: 'Resource Source',
  uploadJobJar: 'Upload Jar File',
  selectJobJar: 'Select Jar File',
  mainClass: 'Main Entry Class',
  project: 'Project',
  module: 'Module',
  appType: 'Job Type',
  programArgs: 'Program Arguments',
  programJar: 'Program Jar File',
  dynamicProperties: 'Dynamic Parameters',
  hadoopConfigTitle: 'System Hadoop',
  dragUploadTitle: 'Click or Drag jar or py to this area to upload',
  dragUploadTip: 'Supports single upload. You can upload local jar here to support the current job',
  dependencyError: 'Please check the Spark version first.',
  status: 'Run Status',
  startTime: 'Start Time',
  endTime: 'End Time',
  hadoopUser: 'Hadoop User',
  restoreModeTip:
    'Flink 1.15 and later supports restore mode; generally, this parameter does not need to be set',
  success: 'Submission Successful',
  appidCheck: 'appId cannot be empty!',
  release: {
    releaseTitle: 'This job is currently starting.',
    releaseDesc: 'Are you sure you want to force another build?',
    releaseFail: 'Job release failed',
    releasing: 'Current job is being released',
  },

  clusterState: {
    created: 'New',
    started: 'Running',
    canceled: 'Stopped',
    lost: 'Lost',
  },
  detail: {
    detailTitle: 'Job Details',
    flinkWebUi: 'Flink Web UI',
    compareConfig: 'Compare Configuration',
    compareSparkSql: 'Compare Spark SQL',
    candidate: 'Candidate',
    compare: 'Compare',
    compareSelectTips: 'Please select the target version',
    resetApi: 'Open API',
    copyCurl: 'Copy CURL',
    apiTitle: 'API Details',
    resetApiToolTip: 'OPEN API, third-party systems can easily integrate with StreamPark',
    copyStartcURL: 'Job Start',
    copyCancelcURL: 'Job Stop',
    apiDocCenter: 'API Documentation',
    nullAccessToken: 'Access token does not exist, please contact the administrator to add',
    invalidAccessToken: 'Access token is invalid, please contact the administrator',
    invalidTokenUser: 'Current user has been locked, please contact the administrator',
    detailTab: {
      detailTabName: {
        option: 'Options',
        configuration: 'Configuration',
        sparkSql: 'Spark SQL',
        backup: 'Backup',
        operationLog: 'Operation Log',
      },
      configDetail: 'View Configuration Details',
      sqlDetail: 'View SQL Details',
      confDeleteTitle: 'Are you sure you want to delete this record?',
      sqlDeleteTitle: 'Are you sure you want to delete this SQL?',
      confBackupTitle: 'Are you sure you want to delete this backup?',
      operationLogDeleteTitle: 'Are you sure you want to delete this operation record?',
      copyPath: 'Copy Path',
      pointDeleteTitle: 'Are you sure you want to delete?',
      copySuccess: 'Successfully copied to clipboard',
      copyFail: 'Copy failed',
      check: 'CheckPoint',
      save: 'SavePoint',
      exception: 'View Exception',
    },
    different: {
      original: 'Original Version',
      target: 'Target Version',
    },
    exceptionModal: {
      title: 'Exception Information',
    },
  },
  view: {
    buildTitle: 'Job Start Progress',
    stepTitle: 'Step Details',
    errorLog: 'Error Log',
    errorSummary: 'Error Summary',
    errorStack: 'Error Stack',
    logTitle: 'Start Log: Job Name [ {0} ]',
    refreshTime: 'Last Refresh Time',
    refresh: 'Refresh',
    start: 'Start Job',
    stop: 'Stop Job',
    recheck: 'The associated project has been updated and needs to re-release this job',
    changed: 'Job has been updated',
    ignoreRestored: 'Ignore Failure',
    ignoreRestoredTip:
      'Skip errors when the state restore fails, allowing the job to continue running, same parameter: -allowNonRestoredState(-n)',
  },
  pod: {
    choice: 'Select',
    init: 'Initialization Content',
    host: 'Host Alias',
  },
  sparkSql: {
    preview: 'Content Preview',
    verify: 'Verify',
    format: 'Format',
    fullScreen: 'Full Screen',
    exit: 'Exit',
    successful: 'Verification Successful',
    compare: 'Compare',
    version: 'Version',
    compareSparkSQL: 'Select Compare Version',
    compareVersionPlaceholder: 'Please select the SQL version to compare',
    effectiveVersion: 'Current Effective Version',
    candidateVersion: 'Candidate Compare Version',
  },
  operation: {
    edit: 'Edit Job',
    release: 'Release Job',
    releaseDetail: 'Release Details',
    start: 'Start Job',
    cancel: 'Stop Job',
    detail: 'Job Details',
    startLog: 'View Flink Start Log',
    abort: 'Terminate Job',
    copy: 'Copy Job',
    remapping: 'Remap Job',
    deleteTip: 'Are you sure you want to delete this job?',
    canceling: 'Current job is stopping',
    starting: 'Current job is starting',
  },

  runStatusOptions: {
    added: 'New',
    starting: 'Starting',
    running: 'Running',
    failed: 'Failed',
    canceled: 'Canceled',
    finished: 'Completed',
    suspended: 'Suspended',
    lost: 'Lost',
    silent: 'Silent',
    terminated: 'Terminated',
  },

  addAppTips: {
    developmentModePlaceholder: 'Please select development mode',
    developmentModeIsRequiredMessage: 'Development mode is required',
    executionModePlaceholder: 'Please select execution mode',
    executionModeIsRequiredMessage: 'Execution mode is required',
    hadoopEnvInitMessage: 'Hadoop environment check failed, please check configuration',
    resourceFromMessage: 'Resource source is required',
    mainClassPlaceholder: 'Please enter the main entry class',
    mainClassIsRequiredMessage: 'Main entry class is required',
    projectPlaceholder: 'Please select a project',
    projectIsRequiredMessage: 'Project is required',
    projectModulePlaceholder: 'Please select the project module',
    appTypePlaceholder: 'Please select job type',
    appTypeIsRequiredMessage: 'Job type is required',
    programJarIsRequiredMessage: 'Program jar file is required',
    useSysHadoopConf: 'Use system Hadoop configuration',
    sparkVersionIsRequiredMessage: 'Spark version is required',
    appNamePlaceholder: 'Please enter job name',
    appNameIsRequiredMessage: 'Job name is required',
    appNameNotUniqueMessage: 'Job name must be unique; the entered job name already exists',
    appNameExistsInYarnMessage:
      'Job name already exists in the YARN cluster and cannot be duplicated. Please check',
    appNameExistsInK8sMessage:
      'The job name already exists in the Kubernetes cluster and cannot be duplicated. Please check',
    appNameValid: 'Job name is invalid',
    appNameRole: 'Job name must follow the following rules:',
    K8sSessionClusterIdRole: 'Kubernetes Cluster ID must follow the following rules:',
    appNameK8sClusterIdRole:
      'When the deployment mode is Kubernetes Application mode, the job name will be used as the Kubernetes cluster ID, so the job name must follow the following rules:',
    appNameK8sClusterIdRoleLength: 'Should not exceed 45 characters',
    appNameK8sClusterIdRoleRegexp:
      'Can only consist of lowercase letters, numbers, characters, and "-" and must satisfy the regex format [a-z]([-a-z0-9]*[a-z0-9])',
    appNameRoleContent:
      'Characters must be (Chinese or English or "-" or "_") and cannot have two consecutive spaces',
    appNameNotValid:
      'Job name is invalid. Characters must be (Chinese or English or "-" or "_") and cannot have two consecutive spaces. Please check',
    flinkClusterIsRequiredMessage: 'Flink cluster is required',
    sparkSqlIsRequiredMessage: 'Spark SQL is required',
    tagsPlaceholder: 'Please enter tags, if more than one, separate with commas (，)',
    parallelismPlaceholder: 'Parallelism for running the program',
    slotsOfPerTaskManagerPlaceholder: 'Number of slots per TaskManager',
    restartSizePlaceholder: 'Maximum restart count',
    alertTemplatePlaceholder: 'Alert Template',
    totalMemoryOptionsPlaceholder: 'Please select the resource parameters to set',
    tmPlaceholder: 'Please select the resource parameters to set',
    yarnQueuePlaceholder: 'Please enter the YARN queue label name',
    descriptionPlaceholder: 'Please enter a description for this job',
    kubernetesNamespacePlaceholder: 'Please enter Kubernetes namespace, e.g., default',
    kubernetesClusterIdPlaceholder: 'Please select Kubernetes Cluster ID',
    kubernetesClusterIdRequire:
      'Lowercase letters, numbers, "-", and must start and end with alphanumeric characters, and not exceed 45 characters',
    kubernetesClusterIdIsRequiredMessage: 'Kubernetes Cluster ID is required',
    flinkImagePlaceholder:
      'Please enter the tag of the Flink base Docker image, e.g., flink:1.13.0-scala_2.11-java8',
    flinkImageIsRequiredMessage: 'Flink base Docker image is required',
    k8sRestExposedTypePlaceholder: 'Kubernetes Rest-Service Exposed Type',
    hadoopXmlConfigFileTips:
      'Automatically copy configuration file from system environment parameters',
    dynamicPropertiesPlaceholder:
      '$key=$value; if there are multiple parameters, you can input them on separate lines (-D <arg>)',
    sqlCheck: 'SQL Check Error',
    sparkAppRequire: 'Spark App is required',
  },

  noteInfo: {
    note: 'Note',
    minute: 'Minute',
    count: 'Count',
    officialDoc: 'Official Documentation',
    checkPointFailureOptions: 'Checkpoint Failure Strategy',
    checkpointFailureRateInterval: 'Checkpoint Failure Interval',
    maxFailuresPerInterval: 'Maximum Failure Count',
    checkPointFailureNote:
      'Checkpoint failure handling strategy, e.g., within 5 minutes (checkpoint failure interval), if the number of checkpoint failures exceeds 10 (maximum checkpoint failure count), it will trigger an operation (send an alert or restart the job)',
    totalMemoryNote:
      'It is not recommended to configure both "total process memory" and "total Flink memory" at the same time. Due to potential memory configuration conflicts, it may lead to deployment failures. Configuring other memory components also requires caution, as it may produce further configuration conflicts; the simplest approach is to set "total process memory"',
    dynamicProperties:
      'Dynamic Properties: Works the same as -D$property=$value in Flink run command line mode, allowing multiple parameters to be specified. For more configurable parameters, please refer to the documentation',
    yarnQueue:
      'This input not only supports quickly setting "yarn.application.name" but also supports setting "yarn.application.node-label". For example, entering "queue1" sets the value of "yarn.application.name" to "queue1", while entering "queue1{\'@\'}label1,label2" sets "yarn.application.name" to "queue1" and "yarn.application.node-label" to "label1,label2". Queue and label are separated by {\'@\'}.',
  },
};
