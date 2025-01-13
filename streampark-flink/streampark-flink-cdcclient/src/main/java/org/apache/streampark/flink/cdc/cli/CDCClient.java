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

package org.apache.streampark.flink.cdc.cli;

import org.apache.streampark.common.conf.ConfigKeys;
import org.apache.streampark.common.util.DeflaterUtils;
import org.apache.streampark.common.util.PropertiesUtils;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.cdc.common.configuration.Configuration;
import org.apache.flink.cdc.composer.PipelineExecution;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.util.StringUtils;
import org.apache.flink.yarn.configuration.YarnConfigOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * cdc client
 */
public class CDCClient {

    private static final Logger LOG = LoggerFactory.getLogger(CDCClient.class);

    public static void main(String[] args) throws Exception {
        ParameterTool parameter = ParameterTool.fromArgs(args);
        Map<String, String> configMap = new HashMap<>();
        String cdcYamlDecode = parameter.get(ConfigKeys.KEY_FLINK_SQL(null));
        String appNameDecode = parameter.get(ConfigKeys.KEY_APP_NAME(null));
        String flinkConfigDecode = parameter.get(ConfigKeys.KEY_FLINK_CONF(null));
        String parallelism = parameter.get(ConfigKeys.KEY_FLINK_PARALLELISM(null));
        if (StringUtils.isNullOrWhitespaceOnly(cdcYamlDecode)
            || StringUtils.isNullOrWhitespaceOnly(appNameDecode)
            || StringUtils.isNullOrWhitespaceOnly(flinkConfigDecode)) {
            LOG.error("--flink.conf or --app.name or `cdc yaml` must not be null.");
            return;
        }

        String cdcYaml = DeflaterUtils.unzipString(cdcYamlDecode);
        String appName = DeflaterUtils.unzipString(appNameDecode);
        String flinkConfigString = DeflaterUtils.unzipString(flinkConfigDecode);
        configMap.putAll(PropertiesUtils.fromYamlTextAsJava(flinkConfigString));
        configMap.put(YarnConfigOptions.APPLICATION_NAME.key(), appName);
        configMap.put(CoreOptions.DEFAULT_PARALLELISM.key(), parallelism);
        Configuration flinkConfig = Configuration.fromMap(configMap);
        LOG.debug("Flink cdc config {}", flinkConfig);
        LOG.debug("Flink cdc yaml {}", cdcYaml);
        PipelineExecution.ExecutionInfo result =
            new CDCExecutor(cdcYaml, flinkConfig, new ArrayList<>(), SavepointRestoreSettings.none()).run();
        printExecutionInfo(result);

    }

    private static void printExecutionInfo(PipelineExecution.ExecutionInfo info) {
        System.out.println("Pipeline has been submitted to cluster.");
        System.out.printf("Job ID: %s\n", info.getId());
        System.out.printf("Job Description: %s\n", info.getDescription());
    }
}
