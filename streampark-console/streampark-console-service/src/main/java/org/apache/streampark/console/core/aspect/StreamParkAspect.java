/*
 * Copyright (c) 2019 The StreamX Project
 *
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

package org.apache.streampark.console.core.aspect;

import org.apache.streampark.console.base.domain.RestResponse;
import org.apache.streampark.console.base.exception.ApiAlertException;
import org.apache.streampark.console.core.annotation.ApiAccess;
import org.apache.streampark.console.core.annotation.CheckApp;
import org.apache.streampark.console.core.annotation.CheckTeam;
import org.apache.streampark.console.core.annotation.CheckUser;
import org.apache.streampark.console.core.entity.Application;
import org.apache.streampark.console.core.enums.UserType;
import org.apache.streampark.console.core.service.ApplicationService;
import org.apache.streampark.console.core.service.CommonService;
import org.apache.streampark.console.core.task.FlinkRESTAPIWatcher;
import org.apache.streampark.console.system.entity.AccessToken;
import org.apache.streampark.console.system.entity.User;
import org.apache.streampark.console.system.service.MemberService;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@Aspect
public class StreamParkAspect {

  @Autowired private FlinkRESTAPIWatcher flinkRESTAPIWatcher;
  @Autowired private CommonService commonService;
  @Autowired private MemberService memberService;
  @Autowired private ApplicationService applicationService;

  @Pointcut(
      "execution(public"
          + " org.apache.streampark.console.base.domain.RestResponse"
          + " org.apache.streampark.console.*.controller.*.*(..))")
  public void apiAccess() {}

  @SuppressWarnings("checkstyle:SimplifyBooleanExpression")
  @Around(value = "apiAccess()")
  public RestResponse apiAccess(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    log.debug("restResponse aspect, method:{}", methodSignature.getName());
    Boolean isApi =
        (Boolean) SecurityUtils.getSubject().getSession().getAttribute(AccessToken.IS_API_TOKEN);
    if (Objects.nonNull(isApi) && isApi) {
      ApiAccess apiAccess = methodSignature.getMethod().getAnnotation(ApiAccess.class);
      if (Objects.isNull(apiAccess) || !apiAccess.value()) {
        throw new ApiAlertException("api accessToken authentication failed!");
      }
    }
    return (RestResponse) joinPoint.proceed();
  }

  @Pointcut("@annotation(org.apache.streampark.console.core.annotation.AppUpdated)")
  public void appUpdated() {}

  @Around("appUpdated()")
  public Object appUpdated(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    log.debug("appUpdated aspect, method:{}", methodSignature.getName());
    Object target = joinPoint.proceed();
    flinkRESTAPIWatcher.init();
    return target;
  }

  @Pointcut("@annotation(org.apache.streampark.console.core.annotation.CheckUser)")
  public void checkUser() {}

  @Around("checkUser()")
  public RestResponse checkUser(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    CheckUser checkUser = methodSignature.getMethod().getAnnotation(CheckUser.class);
    String spELString = checkUser.value();

    Long paramUserId = getId(joinPoint, methodSignature, spELString);
    User currentUser = commonService.getCurrentUser();
    if (currentUser == null
        || (currentUser.getUserType() != UserType.ADMIN
            && !currentUser.getUserId().equals(paramUserId))) {
      throw new ApiAlertException(
          "Permission denied, only ADMIN user or user himself can access this permission");
    }

    return (RestResponse) joinPoint.proceed();
  }

  @Pointcut("@annotation(org.apache.streampark.console.core.annotation.CheckTeam)")
  public void checkTeam() {}

  @Around("checkTeam()")
  public RestResponse checkTeam(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    CheckTeam checkTeam = methodSignature.getMethod().getAnnotation(CheckTeam.class);
    String spELString = checkTeam.value();

    Long paramTeamId = getId(joinPoint, methodSignature, spELString);
    User currentUser = commonService.getCurrentUser();
    if (currentUser == null
        || (currentUser.getUserType() != UserType.ADMIN
            && memberService.findByUserName(paramTeamId, currentUser.getUsername()) == null)) {
      throw new ApiAlertException(
          "Permission denied, only ADMIN user or user belongs to this team can access this permission");
    }

    return (RestResponse) joinPoint.proceed();
  }

  @Pointcut("@annotation(org.apache.streampark.console.core.annotation.CheckApp)")
  public void checkApp() {}

  @Around("checkApp()")
  public RestResponse checkApp(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    CheckApp checkApp = methodSignature.getMethod().getAnnotation(CheckApp.class);
    String spELString = checkApp.value();

    Long paramAppId = getId(joinPoint, methodSignature, spELString);
    Application app = applicationService.getById(paramAppId);
    User currentUser = commonService.getCurrentUser();
    if (currentUser == null
        || (app != null
            && currentUser.getUserType() != UserType.ADMIN
            && memberService.findByUserName(app.getTeamId(), currentUser.getUsername()) == null)) {
      throw new ApiAlertException(
          "Permission denied, only ADMIN user or user belongs to this team can access this permission");
    }

    return (RestResponse) joinPoint.proceed();
  }

  private Long getId(
      ProceedingJoinPoint joinPoint, MethodSignature methodSignature, String spELString) {
    SpelExpressionParser parser = new SpelExpressionParser();
    Expression expression = parser.parseExpression(spELString);
    EvaluationContext context = new StandardEvaluationContext();
    Object[] args = joinPoint.getArgs();
    DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
    String[] parameterNames = discoverer.getParameterNames(methodSignature.getMethod());
    for (int i = 0; i < parameterNames.length; i++) {
      context.setVariable(parameterNames[i], args[i]);
    }
    Object value = expression.getValue(context);

    if (value == null || StringUtils.isBlank(value.toString())) {
      return null;
    }

    try {
      return Long.parseLong(value.toString());
    } catch (NumberFormatException e) {
      throw new ApiAlertException(
          "Wrong use of annotation on method " + methodSignature.getName(), e);
    }
  }
}
