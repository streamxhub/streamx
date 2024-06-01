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
  id: '作业ID',
  appName: '作业名称',
  searchName: '作业名称',
  tags: '作业标签',
  owner: '创建者',
  flinkVersion: 'Flink版本',
  duration: '运行时长',
  modifiedTime: '修改时间',
  runStatus: '运行状态',
  releaseBuild: '发布状态',
  jobType: '作业类型',
  developmentMode: '作业模式',
  executionMode: '执行模式',
  historyVersion: '历史版本',
  resource: '资源',
  resourcePlaceHolder: '从选择资源',
  selectAppPlaceHolder: '选择作业',
  dependency: '作业依赖',
  appConf: '作业配置',
  resolveOrder: '类加载顺序',
  parallelism: '并行度',
  restartSize: '(失败后)重启次数',
  faultAlertTemplate: '告警模板',
  checkPointFailureOptions: 'Checkpoint告警策略',
  totalMemoryOptions: '总内存',
  jmMemoryOptions: 'JM内存',
  tmMemoryOptions: 'TM内存',
  podTemplate: 'Kubernetes Pod 模板',
  flinkCluster: 'Flink集群',
  yarnQueue: 'Yarn队列',
  mavenPom: 'maven pom',
  uploadJar: '上传依赖Jar文件',
  kubernetesNamespace: 'K8S命名空间',
  kubernetesClusterId: 'K8S ClusterId',
  flinkBaseDockerImage: 'Flink基础docker镜像',
  restServiceExposedType: 'K8S服务对外类型',
  resourceFrom: '资源来源',
  uploadJobJar: '上传jar文件',
  selectJobJar: '选择jar文件',
  mainClass: '程序入口类',
  project: '项目',
  module: '模块',
  appType: '作业类型',
  programArgs: '程序参数',
  programJar: '程序Jar文件',
  dynamicProperties: '动态参数',
  hadoopConfigTitle: '系统 Hadoop',
  dragUploadTitle: '单击或拖动 jar或py 到此区域以上传',
  dragUploadTip: '支持单次上传。您可以在此处上传本地 jar 以支持当前作业',
  dependencyError: '请先检查flink 版本.',
  status: '运行状态',
  startTime: '启动时间',
  endTime: '结束时间',
  hadoopUser: 'Hadoop User',
  restoreModeTip: 'flink 1.15开始支持restore模式，一般情况下不用设置该参数',
  release: {
    releaseTitle: '该应用程序的当前启动正在进行中.',
    releaseDesc: '您确定要强制进行另一次构建吗',
    releaseFail: '发布作业失败',
    releasing: '当前作业正在发布中',
  },
  runState: {
    added: '新增作业',
    initializing: '初始化',
    created: '创建中',
    starting: '启动中',
    restarting: '重启中',
    running: '运行中',
    failing: '失败中',
    failed: '作业失败',
    lost: '作业失联',
    cancelling: '取消中',
    canceled: '已取消',
    finished: '已完成',
    suspended: '已暂停',
    reconciling: '调整中',
    mapping: '映射中',
    silent: '沉默',
    terminated: '终止',
  },
  clusterState: {
    created: '新增',
    started: '运行中',
    canceled: '停止',
    lost: '失联',
  },
  detail: {
    detailTitle: '作业详情',
    flinkWebUi: 'Flink Web UI',
    compareConfig: '比较配置',
    compareFlinkSql: '比较 Flink SQL',
    candidate: '侯选',
    compare: '比较',
    compareSelectTips: '请选择目标板本',
    resetApi: '外部系统调用API',
    resetApiToolTip: 'Rest API外部调用接口，其他第三方系统可轻松对接StreamPark',
    copyStartcURL: '复制启动 cURL',
    copyCancelcURL: '复制取消 cURL',
    apiDocCenter: 'Api文档',
    nullAccessToken: '访问令牌为空，请联系管理员添加.',
    invalidAccessToken: '访问令牌无效，请联系管理员。',
    detailTab: {
      detailTabName: {
        option: '选项',
        configuration: '配置',
        flinkSql: 'Flink SQL',
        savepoint: 'Savepoint',
        backup: '备份',
        operationLog: '操作日志',
      },
      configDetail: '查看配置详情',
      sqlDetail: '查看 SQL 详情',
      confDeleteTitle: '您确定要删除此记录吗?',
      sqlDeleteTitle: '您确定要删除此 SQL 吗?',
      confBackupTitle: '您确定要删除该备份吗?',
      operationLogDeleteTitle: '您确定要删除该操作记录吗?',
      copyPath: '复制路径',
      pointDeleteTitle: '您确定要删除?',
      copySuccess: '已成功复制到剪贴板',
      copyFail: '复制失败',
      check: 'CheckPoint',
      save: 'SavePoint',
      exception: '查看异常',
    },
    different: {
      original: '原始版本',
      target: '目标版本',
    },
    exceptionModal: {
      title: '异常信息',
    },
  },
  view: {
    buildTitle: '作业启动进度',
    stepTitle: '步骤详情',
    errorLog: '错误日志',
    errorSummary: '错误摘要',
    errorStack: '错误堆栈',
    logTitle: '启动日志 : 应用名称 [ {0} ]',
    refreshTime: '上次刷新时间',
    refresh: '刷新',
    start: '启动作业',
    stop: '停止应用',
    savepoint: '触发 Savepoint',
    recheck: '关联的项目已更新，需要重新发布此作业',
    changed: '作业已更新',
    fromSavepoint: 'Savepoint 恢复',
    savepointTip: '作业从 savepoint 或 checkpoint 恢复状态',
    savepointInput: '指定 savepoint/checkpoint 路径',
    savepointSwitch: '指定 savepoint/checkpoint 路径 (双击切换"下拉框选择"或"手动输入")',
    ignoreRestored: '忽略失败',
    ignoreRestoredTip: '当状态恢复失败时跳过错误，作业继续运行, 同参数：-allowNonRestoredState(-n)',
  },
  pod: {
    choice: '选择',
    init: '初始化内容',
    host: 'Host别名',
  },
  flinkSql: {
    preview: '内容预览',
    verify: '验证',
    format: '格式化',
    fullScreen: '全屏',
    exit: '退出',
    successful: '验证成功',
    compare: '比较',
    version: '版本',
    compareFlinkSQL: '选择比对版本',
    compareVersionPlaceholder: '请选择要比较的sql版本',
    effectiveVersion: '当前生效版本',
    candidateVersion: '候选比对版本',
  },
  editStreamPark: {
    success: '更新成功',
    flinkSqlRequired: 'Flink Sql 为必填项',
    appidCheck: 'appid 不能为空',
    sqlCheck: 'SQL 检查错误',
  },
  operation: {
    edit: '编辑作业',
    release: '发布作业',
    releaseDetail: '发布详情',
    start: '启动作业',
    cancel: '取消作业',
    savepoint: '触发 Savepoint',
    detail: '作业详情',
    startLog: '查看 Flink 启动日志',
    force: '强制停止作业',
    copy: '复制作业',
    remapping: '重新映射作业',
    deleteTip: '你确定要删除这个作业?',
    triggerSavePoint: '触发 savepoint',
    enableSavePoint: '在 Flink 作业取消之前触发 savepoint',
    customSavepoint: '指定 savepoint 保存路径',
    enableDrain: '在触发 savepoint 和停止作业之前发送 MAX_WATERMARK',
    invalidSavePoint: 'savepoint 路径无效: ',
    canceling: '当前作业正在停止中',
    starting: '当前作业正在启动中',
  },
  dashboard: {
    availableTaskSlots: '可用的任务槽数',
    taskSlots: '任务槽数',
    taskManagers: 'Task Managers',
    runningJobs: '运行的作业',
    totalTask: '总任务',
    runningTask: '运行的任务',
    jobManagerMemory: 'JobManager内存',
    totalJobManagerMemory: 'JobManager总内存',
    taskManagerMemory: 'TaskManager内存',
    totalTaskManagerMemory: 'TaskManager总内存',
  },
  runStatusOptions: {
    added: '新增',
    starting: '启动中',
    running: '运行中',
    failed: '失败',
    canceled: '已取消',
    finished: '已完成',
    suspended: '暂停',
    lost: '丢失',
    silent: '静默',
    terminated: '终止',
  },
  releaseState: {
    failed: '发布失败',
    success: '发布成功',
    waiting: '待发布',
    releasing: '发布中',
    pending: '待回滚',
  },
  addAppTips: {
    developmentModePlaceholder: '请选择开发模式',
    developmentModeIsRequiredMessage: '开发模式必填',
    executionModePlaceholder: '请选择执行模式',
    executionModeIsRequiredMessage: '执行模式必填',
    hadoopEnvInitMessage: 'hadoop环境检查失败, 请检查配置',
    resourceFromMessage: '资源来源必填',
    mainClassPlaceholder: '请输入程序入口类',
    mainClassIsRequiredMessage: '入程序入口类必填',
    projectPlaceholder: '请选择项目',
    projectIsRequiredMessage: '项目必填',
    projectModulePlaceholder: '请选择项目的模块',
    appTypePlaceholder: '请选择作业类型',
    appTypeIsRequiredMessage: '作业类型必填',
    programJarIsRequiredMessage: '程序jar文件必填',
    useSysHadoopConf: '使用系统hadoop配置',
    flinkVersionIsRequiredMessage: 'Flink版本必填',
    appNamePlaceholder: '请输入作业名称',
    appNameIsRequiredMessage: '作业名称必填',
    appNameNotUniqueMessage: '作业名称必须唯一, 输入的作业名称已经存在',
    appNameExistsInYarnMessage: '应用程序名称已经在YARN集群中存在，不能重复。请检查',
    appNameExistsInK8sMessage: '该应用程序名称已经在K8S集群中存在，不能重复。请检查',
    appNameNotValid:
      '应用程序名称无效。字符必须是(中文 或 英文 或 "-" 或 "_")，不能出现两个连续的空格，请检查',
    flinkClusterIsRequiredMessage: 'Flink集群必填',
    flinkSqlIsRequiredMessage: 'Flink SQL必填',
    tagsPlaceholder: '请输入标签，如果超过一个，用逗号（，）分隔',
    parallelismPlaceholder: '运行程序的并行度',
    slotsOfPerTaskManagerPlaceholder: '每个TaskManager的插槽数',
    restartSizePlaceholder: '最大重启次数',
    alertTemplatePlaceholder: '告警模板',
    totalMemoryOptionsPlaceholder: '请选择要设置的资源参数',
    tmPlaceholder: '请选择要设置的资源参数',
    yarnQueuePlaceholder: '请输入yarn队列标签名称',
    descriptionPlaceholder: '请输入此应用程序的描述',
    kubernetesNamespacePlaceholder: '请输入K8S命名空间, 如: default',
    kubernetesClusterIdPlaceholder: '请选择K8S ClusterId',
    kubernetesClusterIdRequire:
      '小写字母、数字、“-”，并且必须以字母数字字符开头和结尾，并且不超过45个字符',
    kubernetesClusterIdIsRequiredMessage: 'K8S ClusterId必填',
    flinkImagePlaceholder: '请输入Flink基础docker镜像的标签,如:flink:1.13.0-scala_2.11-java8',
    flinkImageIsRequiredMessage: 'Flink基础docker镜像是必填的',
    k8sRestExposedTypePlaceholder: 'K8S服务对外类型',
    hadoopXmlConfigFileTips: '从系统环境参数自动复制配置文件',
    dynamicPropertiesPlaceholder: '$key=$value,如果有多个参数，可以换行输入(-D <arg>)',
  },

  noteInfo: {
    note: '提示',
    minute: '分钟',
    count: '次数',
    officialDoc: '官网文档',
    checkPointFailureOptions: 'CheckPoint失败策略',
    checkpointFailureRateInterval: 'checkpoint失败间隔',
    maxFailuresPerInterval: '最大失败次数',
    checkPointFailureNote:
      'checkpoint 失败处理策略, 例如: 在 5 分钟内 (checkpoint的失败间隔), 如果 checkpoint 失败次数超过 10 次 (checkpoint最大失败次数),会触发操作(发送告警或者重启作业)',
    totalMemoryNote:
      '不建议同时配置 "total process memory" 和 "total Flink memory"。 由于潜在的内存配置冲突，它可能会导致部署失败。 配置其他内存组件也需要谨慎，因为它会产生进一步的配置冲突，最简单的方法是设置"total process memory"',
    dynamicProperties:
      '动态Properties: 与flink run命令行模式下的 -D$property=$value 的作用相同，允许指定多个参数。 更多可以设置的参数请查阅',
    yarnQueue:
      '此输入不仅支持快速设置 "yarn.application.name" 还支持设置 "yarn.application.node-label"。例如，输入 "queue1" 表示 "yarn.application.name" 的值为 "queue1"，而输入 "queue1{\'@\'}label1,label2" 则表示 "yarn.application.name" 设置为 "queue1" 且 "yarn.application.node-label" 设置为 "label1,label2"。Queue 和 label 之间使用 {\'@\'} 分隔。',
  },
};
