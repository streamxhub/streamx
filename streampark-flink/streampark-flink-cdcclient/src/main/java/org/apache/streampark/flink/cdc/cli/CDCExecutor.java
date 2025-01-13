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

import org.apache.flink.cdc.cli.parser.PipelineDefinitionParser;
import org.apache.flink.cdc.cli.parser.YamlPipelineDefinitionParser;
import org.apache.flink.cdc.cli.utils.FlinkEnvironmentUtils;
import org.apache.flink.cdc.common.configuration.Configuration;
import org.apache.flink.cdc.composer.PipelineComposer;
import org.apache.flink.cdc.composer.PipelineExecution;
import org.apache.flink.cdc.composer.definition.PipelineDef;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;

import java.nio.file.Path;
import java.util.List;

/**
 * cdc executor
 */
public class CDCExecutor {

    private final String pipelineString;
    private final Configuration configuration;
    private final SavepointRestoreSettings savePointSettings;
    private final List<Path> additionalJar;

    private PipelineComposer composer;

    public CDCExecutor(String pipelineString,
                       Configuration flinkConfig,
                       List<Path> additionalJar,
                       SavepointRestoreSettings savePointRestoreSettings) {
        this.pipelineString = pipelineString;
        this.configuration = flinkConfig;
        this.additionalJar = additionalJar;
        this.savePointSettings = savePointRestoreSettings;
    }

    public PipelineExecution.ExecutionInfo run() throws Exception {
        PipelineDefinitionParser pipelineDefinitionParser = new YamlPipelineDefinitionParser();
        PipelineDef pipelineDef = pipelineDefinitionParser.parse(pipelineString, configuration);
        PipelineComposer composer = getComposer();
        PipelineExecution execution = composer.compose(pipelineDef);
        return execution.execute();
    }

    private PipelineComposer getComposer() throws Exception {
        if (composer == null) {
            return FlinkEnvironmentUtils.createComposer(
                true, configuration, additionalJar, savePointSettings);
        }
        return composer;
    }
}
