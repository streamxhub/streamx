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

package org.apache.streampark.console.core.service.impl;

import org.apache.streampark.console.base.domain.Result;
import org.apache.streampark.console.core.bean.DockerConfig;
import org.apache.streampark.console.core.bean.MavenConfig;
import org.apache.streampark.console.core.bean.SenderEmail;
import org.apache.streampark.console.core.entity.Setting;
import org.apache.streampark.console.core.mapper.SettingMapper;
import org.apache.streampark.console.core.service.SettingService;

import org.apache.commons.lang3.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.AuthResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class SettingServiceImpl extends ServiceImpl<SettingMapper, Setting>
    implements
        SettingService {

    private final Setting emptySetting = new Setting();

    @PostConstruct
    public void loadSettings() {
        list().forEach(x -> SETTINGS.put(x.getSettingKey(), x));
    }

    @Override
    public Setting get(String key) {
        LambdaQueryWrapper<Setting> queryWrapper = new LambdaQueryWrapper<Setting>().eq(Setting::getSettingKey, key);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean update(Setting setting) {
        try {
            String value = StringUtils.trimToNull(setting.getSettingValue());
            setting.setSettingValue(value);

            Setting entity = new Setting();
            entity.setSettingValue(setting.getSettingValue());
            LambdaQueryWrapper<Setting> queryWrapper = new LambdaQueryWrapper<Setting>().eq(Setting::getSettingKey,
                setting.getSettingKey());
            this.update(entity, queryWrapper);

            getMavenConfig().updateConfig();

            Optional<Setting> optional = Optional.ofNullable(SETTINGS.get(setting.getSettingKey()));
            optional.ifPresent(x -> x.setSettingValue(value));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public MavenConfig getMavenConfig() {
        return MavenConfig.fromSetting();
    }

    @Override
    public DockerConfig getDockerConfig() {
        List<Setting> settingList = baseMapper.querySettingByKeys(Arrays.asList(
            SettingService.KEY_DOCKER_REGISTER_ADDRESS,
            SettingService.KEY_DOCKER_REGISTER_USER,
            SettingService.KEY_DOCKER_REGISTER_PASSWORD,
            SettingService.KEY_DOCKER_REGISTER_NAMESPACE));
        DockerConfig dockerConfig = new DockerConfig();
        settingList.forEach(setting -> {
            switch (setting.getSettingKey()) {
                case SettingService.KEY_DOCKER_REGISTER_ADDRESS:
                    dockerConfig.setAddress(setting.getSettingValue());
                    break;
                case SettingService.KEY_DOCKER_REGISTER_USER:
                    dockerConfig.setUsername(setting.getSettingValue());
                    break;
                case SettingService.KEY_DOCKER_REGISTER_PASSWORD:
                    dockerConfig.setPassword(setting.getSettingValue());
                    break;
                case SettingService.KEY_DOCKER_REGISTER_NAMESPACE:
                    dockerConfig.setNamespace(setting.getSettingValue());
                    break;
                default:
                    break;
            }
        });
        return dockerConfig;
    }

    @Override
    public String getStreamParkAddress() {
        return SETTINGS
            .getOrDefault(SettingService.KEY_STREAMPARK_ADDRESS, emptySetting)
            .getSettingValue();
    }

    @Override
    public String getIngressModeDefault() {
        return SETTINGS
            .getOrDefault(SettingService.KEY_INGRESS_MODE_DEFAULT, emptySetting)
            .getSettingValue();
    }

    @Override
    public Result<?> checkDocker(DockerConfig dockerConfig) {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withRegistryUrl(dockerConfig.getAddress())
            .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder().dockerHost(config.getDockerHost()).build();

        try (DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient)) {
            AuthConfig authConfig = new AuthConfig()
                .withUsername(dockerConfig.getUsername())
                .withPassword(dockerConfig.getPassword())
                .withRegistryAddress(dockerConfig.getAddress());
            AuthResponse response = dockerClient.authCmd().withAuthConfig(authConfig).exec();
            if (response.getStatus().equals("Login Succeeded")) {
                return Result.success();
            } else {
                return Result.fail("docker login failed, status: " + response.getStatus());
            }
        } catch (Exception e) {
            if (e.getMessage().contains("LastErrorException")) {
                return Result.fail(null, null, 400);
            } else if (e.getMessage().contains("Status 401")) {
                return Result.fail(
                    "Failed to validate Docker registry, unauthorized: incorrect username or password ");
            } else {
                return Result.fail("Failed to validate Docker registry, error: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean updateDocker(DockerConfig dockerConfig) {
        List<Setting> settings = DockerConfig.toSettings(dockerConfig);
        for (Setting each : settings) {
            if (!update(each)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public SenderEmail getSenderEmail() {
        try {
            String host = SETTINGS.get(SettingService.KEY_ALERT_EMAIL_HOST).getSettingValue();
            String port = SETTINGS.get(SettingService.KEY_ALERT_EMAIL_PORT).getSettingValue();
            String from = SETTINGS.get(SettingService.KEY_ALERT_EMAIL_FROM).getSettingValue();
            String userName = SETTINGS.get(SettingService.KEY_ALERT_EMAIL_USERNAME).getSettingValue();
            String password = SETTINGS.get(SettingService.KEY_ALERT_EMAIL_PASSWORD).getSettingValue();
            String ssl = SETTINGS.get(SettingService.KEY_ALERT_EMAIL_SSL).getSettingValue();

            SenderEmail senderEmail = new SenderEmail();
            senderEmail.setHost(host);
            if (StringUtils.isNotBlank(port)) {
                senderEmail.setPort(Integer.parseInt(port));
            }
            senderEmail.setFrom(from);
            senderEmail.setUserName(userName);
            senderEmail.setPassword(password);
            if (StringUtils.isNotBlank(ssl)) {
                senderEmail.setSsl(Boolean.parseBoolean(ssl));
            }
            return senderEmail;
        } catch (Exception e) {
            log.error("Fault Alert Email is not set.");
        }
        return null;
    }

    @Override
    public Result<?> checkEmail(SenderEmail senderEmail) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        if (senderEmail.isSsl()) {
            props.put("mail.smtp.starttls.enable", "true");
        }
        props.put("mail.smtp.host", senderEmail.getHost());
        props.put("mail.smtp.port", senderEmail.getPort());

        Session session = Session.getInstance(props);
        try {
            Transport transport = session.getTransport("smtp");
            transport.connect(
                senderEmail.getHost(), senderEmail.getUserName(), senderEmail.getPassword());
            transport.close();
            return Result.success();
        } catch (MessagingException e) {
            return Result.fail("connect to target mail server failed: " + e.getMessage());
        }
    }

    @Override
    public boolean updateEmail(SenderEmail senderEmail) {
        List<Setting> settings = SenderEmail.toSettings(senderEmail);
        for (Setting each : settings) {
            if (!update(each)) {
                return false;
            }
        }
        return true;
    }
}
