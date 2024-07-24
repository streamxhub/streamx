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

package org.apache.streampark.console.core.controller;

import org.apache.streampark.console.base.domain.RestResponse;
import org.apache.streampark.console.core.annotation.OpenAPI;
import org.apache.streampark.console.core.annotation.Permission;
import org.apache.streampark.console.core.bean.OpenAPISchema;
import org.apache.streampark.console.core.component.OpenAPIComponent;
import org.apache.streampark.console.core.entity.Application;
import org.apache.streampark.console.core.service.application.ApplicationActionService;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@Slf4j
@Validated
@RestController
@RequestMapping("openapi")
public class OpenAPIController {

    @Autowired
    private OpenAPIComponent openAPIComponent;

    @Autowired
    private ApplicationActionService applicationActionService;

    @OpenAPI(name = "flinkStart", param = {
            @OpenAPI.Param(name = "Authorization", description = "Access authorization token", required = true, type = String.class),
            @OpenAPI.Param(name = "appId", description = "current flink application id", required = true, type = Long.class),
            @OpenAPI.Param(name = "teamId", description = "current user teamId", required = true, type = Long.class),
            @OpenAPI.Param(name = "savePointed", description = "restored app from the savepoint or latest checkpoint", required = false, type = String.class),
            @OpenAPI.Param(name = "savePoint", description = "savepoint or checkpoint path", required = false, type = String.class),
            @OpenAPI.Param(name = "allowNonRestored", description = "ignore savepoint if cannot be restored", required = false, type = boolean.class)
    })
    @Permission(app = "#app.appId", team = "#app.teamId")
    @PostMapping(value = "app/start")
    @RequiresPermissions("app:start")
    public RestResponse start(Application app) throws Exception {
        app.setId(app.getAppId());
        applicationActionService.start(app, false);
        return RestResponse.success(true);
    }

    @OpenAPI(name = "flinkCancel", param = {
            @OpenAPI.Param(name = "Authorization", description = "Access authorization token", required = true, type = String.class),
            @OpenAPI.Param(name = "appId", description = "current flink application id", required = true, type = Long.class),
            @OpenAPI.Param(name = "teamId", description = "current user teamId", required = true, type = Long.class),
            @OpenAPI.Param(name = "savePointed", description = "trigger savepoint before taking stopping", required = false, type = boolean.class),
            @OpenAPI.Param(name = "savePoint", description = "savepoint path", required = false, type = String.class),
            @OpenAPI.Param(name = "drain", description = "send max watermark before canceling", required = false, type = boolean.class),
    })
    @Permission(app = "#app.id", team = "#app.teamId")
    @PostMapping(value = "app/cancel")
    @RequiresPermissions("app:cancel")
    public RestResponse cancel(Application app) throws Exception {
        app.setId(app.getAppId());
        applicationActionService.cancel(app);
        return RestResponse.success();
    }

    /**
     * copy cURL, hardcode now, there is no need for configuration here, because there are several
     * fixed interfaces
     */
    @PostMapping(value = "curl")
    public RestResponse copyOpenApiCurl(
                                        @NotBlank(message = "{required}") String baseUrl,
                                        @NotBlank(message = "{required}") Long appId,
                                        @NotBlank(message = "{required}") Long teamId,
                                        @NotBlank(message = "{required}") String name) {
        String url = openAPIComponent.getOpenApiCUrl(baseUrl, appId, teamId, name);
        return RestResponse.success(url);
    }

    @PostMapping(value = "schema")
    public RestResponse schema(@NotBlank(message = "{required}") String name) {
        OpenAPISchema openAPISchema = openAPIComponent.getOpenAPISchema(name);
        return RestResponse.success(openAPISchema);
    }

}