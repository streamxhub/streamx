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

package org.apache.streampark.console.core.service;

import org.apache.streampark.console.SpringTestBase;
import org.apache.streampark.console.base.exception.ApiAlertException;
import org.apache.streampark.console.core.entity.Resource;
import org.apache.streampark.console.core.enums.EngineType;
import org.apache.streampark.console.core.enums.ResourceType;
import org.apache.streampark.console.core.enums.UserType;
import org.apache.streampark.console.system.entity.User;
import org.apache.streampark.console.system.service.UserService;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** org.apache.streampark.console.core.service.UserServiceTest. */
class UserServiceTest extends SpringTestBase {
  @Autowired private UserService userService;

  @Test
  void testLockUser() throws Exception {
    val user = new User();
    user.setUsername("test");
    user.setNickName("test");
    user.setPassword("test");
    user.setUserType(UserType.USER);
    user.setStatus(User.STATUS_VALID);
    Db.save(user);
    Assertions.assertDoesNotThrow(
        () -> {
          // lock user
          user.setStatus(User.STATUS_LOCK);
          userService.updateUser(user);
          // unlock user
          user.setStatus(User.STATUS_VALID);
          userService.updateUser(user);
        });
    val resource = new Resource();
    resource.setResourceName("test");
    resource.setResourceType(ResourceType.FLINK_APP);
    resource.setEngineType(EngineType.FLINK);
    resource.setTeamId(1L);
    resource.setCreatorId(user.getUserId());
    Db.save(resource);
    Assertions.assertThrows(
        ApiAlertException.class,
        () -> {
          // lock user when has resource
          user.setStatus(User.STATUS_LOCK);
          userService.updateUser(user);
        });
  }
}
