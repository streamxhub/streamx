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
  title: 'Spark 集群',
  detail: '查看集群详情',
  stop: '停止集群',
  start: '开启集群',
  edit: '编辑集群',
  delete: '确定要删除此集群 ?',
  searchByName: '根据 Spark 集群名称搜索',
  form: {
    clusterName: '集群名称',
    address: '集群URL',
    runState: '运行状态',
    deployMode: '部署模式',
    versionId: 'Spark版本',
    addType: '添加类型',
    addExisting: '已有集群',
    addNew: '全新集群',
    yarnQueue: 'Yarn队列',
    yarnSessionClusterId: 'Yarn Session模式集群ID',
    k8sNamespace: 'Kubernetes 命名空间',
    k8sClusterId: 'Kubernetes 集群 ID',
    serviceAccount: 'Kubernetes  服务账号',
    k8sConf: 'Kube 配置文件',
    flinkImage: 'Spark 基础 Docker 镜像',
    k8sRestExposedType: 'Kubernetes Rest exposed-type',
    resolveOrder: '类加载顺序',
    taskSlots: '任务槽数',
    jmOptions: 'JM内存',
    tmOptions: 'TM内存',
    dynamicProperties: '动态参数',
    clusterDescription: '集群描述',
  },
};
