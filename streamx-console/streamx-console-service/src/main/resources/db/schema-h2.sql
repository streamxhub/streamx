/*
 * Copyright 2019 The StreamX Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

-- ----------------------------
-- Table structure for t_app_backup
-- ----------------------------
create table if not exists `t_app_backup` (
  `id` bigint generated by default as identity not null,
  `app_id` bigint default null,
  `sql_id` bigint default null,
  `config_id` bigint default null,
  `version` int default null,
  `path` varchar(255)  default null,
  `description` varchar(255) default null,
  `create_time` datetime default null,
  primary key(`id`)
);

-- ----------------------------
-- Table structure for t_flame_graph
-- ----------------------------
create table if not exists `t_flame_graph` (
  `id` bigint generated by default as identity not null,
  `app_id` bigint default null,
  `profiler` varchar(255) default null,
  `timeline` datetime default null,
  `content` text,
  primary key(`id`)
);

-- ----------------------------
-- Table structure for t_flink_app
-- ----------------------------
create table if not exists `t_flink_app` (
  `id` bigint generated by default as identity not null,
  `job_type` tinyint default null,
  `execution_mode` tinyint default null,
  `resource_from` tinyint default null,
  `project_id` varchar(64)  default null,
  `job_name` varchar(255)  default null,
  `module` varchar(255)  default null,
  `jar` varchar(255)  default null,
  `jar_check_sum` bigint default null,
  `main_class` varchar(255)  default null,
  `args` text,
  `options` text,
  `hot_params` text ,
  `user_id` bigint default null,
  `app_id` varchar(255)  default null,
  `app_type` tinyint default null,
  `duration` bigint default null,
  `job_id` varchar(64)  default null,
  `version_id` bigint default null,
  `cluster_id` varchar(255)  default null,
  `k8s_namespace` varchar(255)  default null,
  `flink_image` varchar(255)  default null,
  `state` varchar(50)  default null,
  `restart_size` int default null,
  `restart_count` int default null,
  `cp_threshold` int default null,
  `cp_max_failure_interval` int default null,
  `cp_failure_rate_interval` int default null,
  `cp_failure_action` tinyint default null,
  `dynamic_options` text ,
  `description` varchar(255)  default null,
  `resolve_order` tinyint default null,
  `k8s_rest_exposed_type` tinyint default null,
  `flame_graph` tinyint default 0,
  `jm_memory` int default null,
  `tm_memory` int default null,
  `total_task` int default null,
  `total_tm` int default null,
  `total_slot` int default null,
  `available_slot` int default null,
  `option_state` tinyint default null,
  `tracking` tinyint default null,
  `create_time` datetime default null,
  `modify_time` datetime not null default current_timestamp,
  `option_time` datetime default null,
  `launch` tinyint default 1,
  `build` tinyint default 1,
  `start_time` datetime default null,
  `end_time` datetime default null,
  `alert_id` bigint default null,
  `k8s_pod_template` text ,
  `k8s_jm_pod_template` text ,
  `k8s_tm_pod_template` text ,
  `k8s_hadoop_integration` tinyint default 0,
  `flink_cluster_id` bigint default null,
  `ingress_template` text ,
  `default_mode_ingress` text ,
  `team_id` bigint not null default 1,
  primary key(`id`)
);

-- ----------------------------
-- Table structure for t_flink_config
-- ----------------------------
create table if not exists `t_flink_config` (
  `id` bigint generated by default as identity not null,
  `app_id` bigint not null,
  `format` tinyint not null default 0,
  `version` int not null,
  `latest` tinyint not null default 0,
  `content` text  not null,
  `create_time` datetime default null,
  primary key(`id`)
);

-- ----------------------------
-- Table structure for t_flink_effective
-- ----------------------------
create table if not exists `t_flink_effective` (
  `id` bigint generated by default as identity not null,
  `app_id` bigint not null,
  `target_type` tinyint not null comment '1) config 2) flink sql',
  `target_id` bigint not null comment 'configId or sqlId',
  `create_time` datetime default null,
  primary key(`id`),
  unique (`app_id`,`target_type`)
);

-- ----------------------------
-- table structure for t_flink_env
-- ----------------------------
create table if not exists `t_flink_env` (
  `id` bigint generated by default as identity not null,
  `flink_name` varchar(128)  not null comment 'flink instance name',
  `flink_home` varchar(255)  not null comment 'flink home path',
  `version` varchar(50)  not null comment 'flink version',
  `scala_version` varchar(50)  not null comment 'scala version of flink',
  `flink_conf` text  not null comment 'flink-conf',
  `is_default` tinyint not null default '0' comment 'whether default version or not',
  `description` varchar(255)  default null comment 'description',
  `create_time` datetime not null comment 'create time',
  primary key(`id`),
  unique (`flink_name`)
);

-- ----------------------------
-- table structure for t_flink_log
-- ----------------------------
create table if not exists `t_flink_log` (
  `id` bigint generated by default as identity not null,
  `app_id` bigint default null,
  `yarn_app_id` varchar(50)  default null,
  `success` tinyint default null,
  `exception` text ,
  `option_time` datetime default null,
  primary key(`id`)
);


-- ----------------------------
-- table structure for t_flink_project
-- ----------------------------
create table if not exists `t_flink_project` (
  `id` bigint generated by default as identity not null,
  `name` varchar(255)  default null,
  `url` varchar(1000)  default null,
  `branches` varchar(1000)  default null,
  `user_name` varchar(255)  default null,
  `password` varchar(255)  default null,
  `pom` varchar(255)  default null,
  `build_args` varchar(255) default null,
  `type` tinyint default null,
  `repository` tinyint default null,
  `last_build` datetime default null,
  `description` varchar(255)  default null,
  `build_state` tinyint default -1,
  `team_id` bigint not null default 1,
  `create_time` datetime default null,
  `modify_time` datetime default null,
  primary key(`id`)
);


-- ----------------------------
-- Table structure for t_flink_savepoint
-- ----------------------------
create table if not exists `t_flink_savepoint` (
  `id` bigint generated by default as identity not null,
  `app_id` bigint not null,
  `type` tinyint default null,
  `path` varchar(255)  default null,
  `latest` tinyint not null default 1,
  `trigger_time` datetime default null,
  `create_time` datetime default null,
  primary key(`id`)
);

-- ----------------------------
-- table structure for t_flink_sql
-- ----------------------------
create table if not exists `t_flink_sql` (
  `id` bigint generated by default as identity not null,
  `app_id` bigint default null,
  `sql` text ,
  `dependency` text ,
  `version` int default null,
  `candidate` tinyint not null default 1,
  `create_time` datetime default null,
  primary key(`id`)
);


-- ----------------------------
-- Table structure for t_flink_tutorial
-- ----------------------------
create table if not exists `t_flink_tutorial` (
  `id` int generated by default as identity not null,
  `type` tinyint default null,
  `name` varchar(255)  default null,
  `content` text ,
  `create_time` datetime default null,
  primary key(`id`)
);

-- ----------------------------
-- Table structure for t_menu
-- ----------------------------
create table if not exists `t_menu` (
  `menu_id` bigint generated by default as identity not null comment  'menu button id',
  `parent_id` bigint not null comment  'parent menu id',
  `menu_name` varchar(50)  not null comment 'menu button name',
  `path` varchar(255)  default null comment 'routing path',
  `component` varchar(255)  default null comment 'routing component component',
  `perms` varchar(50)  default null comment 'authority id',
  `icon` varchar(50)  default null comment 'icon',
  `type` char(2)  default null comment 'type 0:menu 1:button',
  `display` tinyint not null default 0 comment 'whether the menu is displayed',
  `order_num` int default null comment 'sort',
  `create_time` datetime not null comment 'create time',
  `modify_time` datetime default null comment 'modify time',
  primary key(`menu_id`)
);

-- ----------------------------
-- Table structure for t_message
-- ----------------------------
create table if not exists `t_message` (
  `id` bigint generated by default as identity not null,
  `app_id` bigint default null,
  `user_id` bigint default null,
  `type` tinyint default null,
  `title` varchar(255)  default null,
  `context` text ,
  `read` tinyint default 0,
  `create_time` datetime default null,
  primary key(`id`)
);

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
create table if not exists `t_role` (
  `role_id` bigint generated by default as identity not null comment 'user id',
  `role_name` varchar(50)  not null comment 'user name',
  `remark` varchar(100)  default null comment 'remark',
  `create_time` datetime not null comment 'create time',
  `modify_time` datetime default null comment 'modify time',
  `role_code` varchar(255)  default null comment 'role code',
  primary key(`role_id`)
);

-- ----------------------------
-- Table structure for t_role_menu
-- ----------------------------
create table if not exists `t_role_menu` (
  `id` bigint generated by default as identity not null,
  `role_id` bigint not null,
  `menu_id` bigint not null,
  primary key(`id`),
  unique (`role_id`,`menu_id`)
);

-- ----------------------------
-- Table structure for t_setting
-- ----------------------------
create table if not exists `t_setting` (
  `order_num` int default null,
  `setting_key` varchar(50) primary key not null,
  `setting_value` text  default null,
  `setting_name` varchar(255)  default null,
  `description` varchar(255)  default null,
  `type` tinyint not null comment '1: input 2: boolean 3: number'
);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
create table if not exists `t_user` (
  `user_id` bigint generated by default as identity not null comment 'user id',
  `username` varchar(255)  default null comment 'user name',
  `nick_name` varchar(50)  not null comment 'nick name',
  `salt` varchar(255)  default null comment 'salt',
  `password` varchar(128)  not null comment 'password',
  `email` varchar(128)  default null comment 'email',
  `status` char(1)  not null comment 'status 0:locked 1:active',
  `create_time` datetime not null comment 'create time',
  `modify_time` datetime default null comment 'change time',
  `last_login_time` datetime default null comment 'last login time',
  `sex` char(1)  default null comment 'gender 0:male 1:female 2:confidential',
  `avatar` varchar(100)  default null comment 'avatar',
  `description` varchar(100)  default null comment 'description',
  primary key(`user_id`),
  unique (`nick_name`)
);

-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
create table if not exists `t_user_role` (
  `id` bigint generated by default as identity not null,
  `user_id` bigint default null comment 'user id',
  `role_id` bigint default null comment 'role id',
  primary key(`id`),
  unique (`user_id`,`role_id`)
);

-- ----------------------------
-- Table of t_app_build_pipe
-- ----------------------------
create table if not exists `t_app_build_pipe` (
  `app_id` bigint generated by default as identity not null,
  `pipe_type` tinyint,
  `pipe_status` tinyint,
  `cur_step` smallint,
  `total_step` smallint,
  `steps_status` text,
  `steps_status_ts` text,
  `error` text,
  `build_result` text,
  `modify_time` datetime,
  primary key(`app_id`)
);

-- ----------------------------
-- Table of t_flink_cluster
-- ----------------------------
create table if not exists `t_flink_cluster` (
  `id` bigint  generated by default as identity not null,
  `address` varchar(255) default null comment 'url address of jobmanager',
  `cluster_id` varchar(255) default null comment 'clusterid of session mode(yarn-session:application-id,k8s-session:cluster-id)',
  `cluster_name` varchar(255) not null comment 'cluster name',
  `options` text comment 'json form of parameter collection ',
  `yarn_queue` varchar(100) default null comment 'the yarn queue where the task is located',
  `execution_mode` tinyint not null default 1 comment 'k8s execution session mode(1:remote,3:yarn-session,5:kubernetes-session)',
  `version_id` bigint not null comment 'flink version id',
  `k8s_namespace` varchar(255) default 'default' comment 'k8s namespace',
  `service_account` varchar(50) default null comment 'k8s service account',
  `description` varchar(255) default null,
  `user_id` bigint default null,
  `flink_image` varchar(255) default null comment 'flink image',
  `dynamic_options` text comment 'dynamic parameters',
  `k8s_rest_exposed_type` tinyint default 2 comment 'k8s export(0:loadbalancer,1:clusterip,2:nodeport)',
  `k8s_hadoop_integration` tinyint default 0,
  `flame_graph` tinyint default 0 comment 'flameGraph enable，default disable',
  `k8s_conf` varchar(255) default null comment 'the path where the k 8 s configuration file is located',
  `resolve_order` tinyint default null,
  `exception` text comment 'exception information',
  `cluster_state` tinyint default '0' comment 'cluster status (0: created but not started, 1: started, 2: stopped)',
  `create_time` datetime default null,
  primary key(`id`,`cluster_name`),
  unique (`cluster_id`,`address`,`execution_mode`)
);


-- ----------------------------
-- Table of t_access_token definition
-- ----------------------------
create table if not exists `t_alert_config` (
  `id` bigint generated by default as identity not null,
  `user_id` bigint default null,
  `alert_name` varchar(128)  default null comment 'alert group name',
  `alert_type` int default 0 comment 'alert type',
  `email_params` varchar(255)  comment 'email params',
  `sms_params` text  comment 'sms params',
  `ding_talk_params` text  comment 'ding talk params',
  `we_com_params` varchar(255)  comment 'wechat params',
  `http_callback_params` text  comment 'http callback params',
  `lark_params` text  comment 'lark params',
  `create_time` datetime not null default current_timestamp comment 'create time',
  `modify_time` datetime not null default current_timestamp on update current_timestamp comment 'change time',
  primary key(`id`)
);


-- ----------------------------
-- Table of t_alert_config
-- ----------------------------
create table if not exists `t_team` (
  `team_id` bigint generated by default as identity not null comment 'id',
  `team_code` varchar(255) not null comment 'team ID, which could be used for queue and resource isolation',
  `team_name` varchar(255) not null comment 'team name',
  `create_time` datetime not null comment 'create time',
  primary key(`team_id`)
);

-- ----------------------------
-- Table of t_team
-- ----------------------------
create table if not exists `t_access_token` (
  `id` int generated by default as identity not null comment 'key',
  `user_id` bigint,
  `token` varchar(1024) default null comment 'token',
  `expire_time` datetime default null comment 'expiration',
  `description` varchar(512) default null comment 'description',
  `status` tinyint default null comment '1:enable,0:disable',
  `create_time` datetime default null comment 'create time',
  `modify_time` datetime default null comment 'modify time',
  primary key(`id`)
);

-- ----------------------------
-- Table of t_team_user
-- ----------------------------
create table if not exists `t_team_user` (
  `team_id` bigint not null,
  `user_id` bigint not null,
  `create_time` datetime not null,
  unique (`team_id`,`user_id`)
);
