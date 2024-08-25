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

import org.apache.streampark.console.core.service.ProxyService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Validated
@RestController
@RequestMapping("proxy")
public class ProxyController {

  @Autowired private ProxyService proxyService;

  @GetMapping("flink/{id}/**")
  public ResponseEntity<?> proxyFlink(HttpServletRequest request, @PathVariable("id") Long id)
      throws Exception {
    return proxyService.proxyFlink(request, id);
  }

  @GetMapping("cluster/{id}/**")
  public ResponseEntity<?> proxyCluster(HttpServletRequest request, @PathVariable("id") Long id)
      throws Exception {
    return proxyService.proxyCluster(request, id);
  }

  @GetMapping("history/{id}/**")
  public ResponseEntity<?> proxyHistory(HttpServletRequest request, @PathVariable("id") Long id)
      throws Exception {
    return proxyService.proxyHistory(request, id);
  }

  @GetMapping("yarn/{id}/**")
  public ResponseEntity<?> proxyYarn(HttpServletRequest request, @PathVariable("id") Long logId)
      throws Exception {
    return proxyService.proxyYarn(request, logId);
  }
}
