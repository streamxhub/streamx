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

import org.apache.streampark.common.conf.ConfigConst;
import org.apache.streampark.common.conf.Workspace;
import org.apache.streampark.common.enums.ExecutionMode;
import org.apache.streampark.common.fs.FsOperator;
import org.apache.streampark.common.util.FileUtils;
import org.apache.streampark.common.util.ThreadUtils;
import org.apache.streampark.common.util.Utils;
import org.apache.streampark.console.base.exception.ApiAlertException;
import org.apache.streampark.console.base.util.WebUtils;
import org.apache.streampark.console.core.entity.AppBuildPipeline;
import org.apache.streampark.console.core.entity.Application;
import org.apache.streampark.console.core.entity.ApplicationConfig;
import org.apache.streampark.console.core.entity.ApplicationLog;
import org.apache.streampark.console.core.entity.FlinkEnv;
import org.apache.streampark.console.core.entity.FlinkSql;
import org.apache.streampark.console.core.entity.Message;
import org.apache.streampark.console.core.enums.CandidateType;
import org.apache.streampark.console.core.enums.NoticeType;
import org.apache.streampark.console.core.enums.OptionState;
import org.apache.streampark.console.core.enums.ReleaseState;
import org.apache.streampark.console.core.mapper.ApplicationBuildPipelineMapper;
import org.apache.streampark.console.core.service.AppBuildPipeService;
import org.apache.streampark.console.core.service.ApplicationBackUpService;
import org.apache.streampark.console.core.service.ApplicationConfigService;
import org.apache.streampark.console.core.service.ApplicationLogService;
import org.apache.streampark.console.core.service.ApplicationService;
import org.apache.streampark.console.core.service.CommonService;
import org.apache.streampark.console.core.service.FlinkEnvService;
import org.apache.streampark.console.core.service.FlinkSqlService;
import org.apache.streampark.console.core.service.MessageService;
import org.apache.streampark.console.core.service.SettingService;
import org.apache.streampark.console.core.task.FlinkRESTAPIWatcher;
import org.apache.streampark.flink.packer.docker.DockerConf;
import org.apache.streampark.flink.packer.maven.MavenTool;
import org.apache.streampark.flink.packer.pipeline.BuildPipeline;
import org.apache.streampark.flink.packer.pipeline.BuildResult;
import org.apache.streampark.flink.packer.pipeline.DockerBuildSnapshot;
import org.apache.streampark.flink.packer.pipeline.DockerProgressWatcher;
import org.apache.streampark.flink.packer.pipeline.DockerPullSnapshot;
import org.apache.streampark.flink.packer.pipeline.DockerPushSnapshot;
import org.apache.streampark.flink.packer.pipeline.DockerResolvedSnapshot;
import org.apache.streampark.flink.packer.pipeline.FlinkK8sApplicationBuildRequest;
import org.apache.streampark.flink.packer.pipeline.FlinkK8sSessionBuildRequest;
import org.apache.streampark.flink.packer.pipeline.FlinkRemotePerJobBuildRequest;
import org.apache.streampark.flink.packer.pipeline.FlinkYarnApplicationBuildRequest;
import org.apache.streampark.flink.packer.pipeline.PipeSnapshot;
import org.apache.streampark.flink.packer.pipeline.PipeWatcher;
import org.apache.streampark.flink.packer.pipeline.PipelineStatus;
import org.apache.streampark.flink.packer.pipeline.PipelineType;
import org.apache.streampark.flink.packer.pipeline.impl.FlinkK8sApplicationBuildPipeline;
import org.apache.streampark.flink.packer.pipeline.impl.FlinkK8sSessionBuildPipeline;
import org.apache.streampark.flink.packer.pipeline.impl.FlinkRemoteBuildPipeline;
import org.apache.streampark.flink.packer.pipeline.impl.FlinkYarnApplicationBuildPipeline;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
public class AppBuildPipeServiceImpl
    extends ServiceImpl<ApplicationBuildPipelineMapper, AppBuildPipeline>
    implements AppBuildPipeService {

  @Autowired private FlinkEnvService flinkEnvService;

  @Autowired private FlinkSqlService flinkSqlService;

  @Autowired private ApplicationBackUpService backUpService;

  @Autowired private CommonService commonService;

  @Autowired private SettingService settingService;

  @Autowired private MessageService messageService;

  @Autowired private ApplicationService applicationService;

  @Autowired private ApplicationLogService applicationLogService;

  @Autowired private FlinkRESTAPIWatcher flinkRESTAPIWatcher;

  @Autowired private ApplicationConfigService applicationConfigService;

  private final ExecutorService executorService =
      new ThreadPoolExecutor(
          Runtime.getRuntime().availableProcessors() * 5,
          Runtime.getRuntime().availableProcessors() * 10,
          60L,
          TimeUnit.SECONDS,
          new LinkedBlockingQueue<>(1024),
          ThreadUtils.threadFactory("streampark-build-pipeline-executor"),
          new ThreadPoolExecutor.AbortPolicy());

  private static final Cache<Long, DockerPullSnapshot> DOCKER_PULL_PG_SNAPSHOTS =
      Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.DAYS).build();

  private static final Cache<Long, DockerBuildSnapshot> DOCKER_BUILD_PG_SNAPSHOTS =
      Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.DAYS).build();

  private static final Cache<Long, DockerPushSnapshot> DOCKER_PUSH_PG_SNAPSHOTS =
      Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.DAYS).build();

  @Override
  public boolean buildApplication(@Nonnull Application app, ApplicationLog applicationLog) {
    // 1) flink sql setDependency
    FlinkSql newFlinkSql = flinkSqlService.getCandidate(app.getId(), CandidateType.NEW);
    FlinkSql effectiveFlinkSql = flinkSqlService.getEffective(app.getId(), false);
    if (app.isFlinkSqlJob()) {
      FlinkSql flinkSql = newFlinkSql == null ? effectiveFlinkSql : newFlinkSql;
      Utils.notNull(flinkSql);
      app.setDependency(flinkSql.getDependency());
    }

    // create pipeline instance
    BuildPipeline pipeline = createPipelineInstance(app);

    // clear history
    removeApp(app.getId());
    // register pipeline progress event watcher.
    // save snapshot of pipeline to db when status of pipeline was changed.
    pipeline.registerWatcher(
        new PipeWatcher() {
          @Override
          public void onStart(PipeSnapshot snapshot) throws Exception {
            AppBuildPipeline buildPipeline =
                AppBuildPipeline.fromPipeSnapshot(snapshot).setAppId(app.getId());
            saveEntity(buildPipeline);

            app.setRelease(ReleaseState.RELEASING.get());
            applicationService.updateRelease(app);

            if (flinkRESTAPIWatcher.isWatchingApp(app.getId())) {
              flinkRESTAPIWatcher.init();
            }

            // 1) checkEnv
            applicationService.checkEnv(app);

            // 2) some preparatory work
            prepareJars(app);
          }

          @Override
          public void onStepStateChange(PipeSnapshot snapshot) {
            AppBuildPipeline buildPipeline =
                AppBuildPipeline.fromPipeSnapshot(snapshot).setAppId(app.getId());
            saveEntity(buildPipeline);
          }

          @Override
          public void onFinish(PipeSnapshot snapshot, BuildResult result) {
            AppBuildPipeline buildPipeline =
                AppBuildPipeline.fromPipeSnapshot(snapshot)
                    .setAppId(app.getId())
                    .setBuildResult(result);
            saveEntity(buildPipeline);
            if (result.pass()) {
              // running job ...
              if (app.isRunning()) {
                app.setRelease(ReleaseState.NEED_RESTART.get());
              } else {
                app.setOptionState(OptionState.NONE.getValue());
                app.setRelease(ReleaseState.DONE.get());
                // If the current task is not running, or the task has just been added, directly set
                // the candidate version to the official version
                if (app.isFlinkSqlJob()) {
                  applicationService.toEffective(app);
                } else {
                  if (app.isStreamParkJob()) {
                    ApplicationConfig config = applicationConfigService.getLatest(app.getId());
                    if (config != null) {
                      config.setToApplication(app);
                      applicationConfigService.toEffective(app.getId(), app.getConfigId());
                    }
                  }
                }
              }
              // backup.
              if (!app.isNeedRollback()) {
                if (app.isFlinkSqlJob() && newFlinkSql != null) {
                  backUpService.backup(app, newFlinkSql);
                } else {
                  backUpService.backup(app, null);
                }
              }
              applicationLog.setSuccess(true);
              app.setBuild(false);

            } else {
              Message message =
                  new Message(
                      commonService.getUserId(),
                      app.getId(),
                      app.getJobName().concat(" release failed"),
                      Utils.stringifyException(snapshot.error().exception()),
                      NoticeType.EXCEPTION);
              messageService.push(message);
              app.setRelease(ReleaseState.FAILED.get());
              app.setOptionState(OptionState.NONE.getValue());
              app.setBuild(true);
              applicationLog.setException(Utils.stringifyException(snapshot.error().exception()));
              applicationLog.setSuccess(false);
            }
            applicationService.updateRelease(app);
            applicationLogService.save(applicationLog);
            if (flinkRESTAPIWatcher.isWatchingApp(app.getId())) {
              flinkRESTAPIWatcher.init();
            }
          }
        });
    // save docker resolve progress detail to cache, only for flink-k8s application mode.
    if (PipelineType.FLINK_NATIVE_K8S_APPLICATION == pipeline.pipeType()) {
      pipeline
          .as(FlinkK8sApplicationBuildPipeline.class)
          .registerDockerProgressWatcher(
              new DockerProgressWatcher() {
                @Override
                public void onDockerPullProgressChange(DockerPullSnapshot snapshot) {
                  DOCKER_PULL_PG_SNAPSHOTS.put(app.getId(), snapshot);
                }

                @Override
                public void onDockerBuildProgressChange(DockerBuildSnapshot snapshot) {
                  DOCKER_BUILD_PG_SNAPSHOTS.put(app.getId(), snapshot);
                }

                @Override
                public void onDockerPushProgressChange(DockerPushSnapshot snapshot) {
                  DOCKER_PUSH_PG_SNAPSHOTS.put(app.getId(), snapshot);
                }
              });
    }
    // save pipeline instance snapshot to db before release it.
    AppBuildPipeline buildPipeline =
        AppBuildPipeline.initFromPipeline(pipeline).setAppId(app.getId());
    boolean saved = saveEntity(buildPipeline);
    DOCKER_PULL_PG_SNAPSHOTS.invalidate(app.getId());
    DOCKER_BUILD_PG_SNAPSHOTS.invalidate(app.getId());
    DOCKER_PUSH_PG_SNAPSHOTS.invalidate(app.getId());
    // async release pipeline
    executorService.submit((Runnable) pipeline::launch);
    return saved;
  }

  /** create building pipeline instance */
  private BuildPipeline createPipelineInstance(@Nonnull Application app) {
    FlinkEnv flinkEnv = flinkEnvService.getByIdOrDefault(app.getVersionId());
    String userLocalJar = retrieveUserLocalJar(flinkEnv, app);
    ExecutionMode executionMode = app.getExecutionModeEnum();
    String mainClass =
        app.isCustomCodeJob() ? app.getMainClass() : ConfigConst.STREAMPARK_FLINKSQL_CLIENT_CLASS();
    switch (executionMode) {
      case YARN_APPLICATION:
        String yarnProvidedPath = app.getAppLib();
        FlinkYarnApplicationBuildRequest yarnAppRequest =
            new FlinkYarnApplicationBuildRequest(
                app.getJobName(),
                mainClass,
                yarnProvidedPath,
                app.getDevelopmentMode(),
                app.getDependencyInfo());
        log.info("Submit params to building pipeline : {}", yarnAppRequest);
        return FlinkYarnApplicationBuildPipeline.of(yarnAppRequest);
      case YARN_PER_JOB:
      case YARN_SESSION:
      case REMOTE:
        FlinkRemotePerJobBuildRequest buildRequest =
            new FlinkRemotePerJobBuildRequest(
                app.getJobName(),
                app.getLocalAppHome(),
                mainClass,
                userLocalJar,
                app.getExecutionModeEnum(),
                app.getDevelopmentMode(),
                flinkEnv.getFlinkVersion(),
                app.getDependencyInfo());
        log.info("Submit params to building pipeline : {}", buildRequest);
        return FlinkRemoteBuildPipeline.of(buildRequest);
      case KUBERNETES_NATIVE_SESSION:
        FlinkK8sSessionBuildRequest k8sSessionBuildRequest =
            new FlinkK8sSessionBuildRequest(
                app.getJobName(),
                app.getLocalAppHome(),
                mainClass,
                userLocalJar,
                app.getExecutionModeEnum(),
                app.getDevelopmentMode(),
                flinkEnv.getFlinkVersion(),
                app.getDependencyInfo(),
                app.getClusterId(),
                app.getK8sNamespace());
        log.info("Submit params to building pipeline : {}", k8sSessionBuildRequest);
        return FlinkK8sSessionBuildPipeline.of(k8sSessionBuildRequest);
      case KUBERNETES_NATIVE_APPLICATION:
        FlinkK8sApplicationBuildRequest k8sApplicationBuildRequest =
            new FlinkK8sApplicationBuildRequest(
                app.getJobName(),
                app.getLocalAppHome(),
                mainClass,
                userLocalJar,
                app.getExecutionModeEnum(),
                app.getDevelopmentMode(),
                flinkEnv.getFlinkVersion(),
                app.getDependencyInfo(),
                app.getClusterId(),
                app.getK8sNamespace(),
                app.getFlinkImage(),
                app.getK8sPodTemplates(),
                app.getK8sHadoopIntegration() != null ? app.getK8sHadoopIntegration() : false,
                DockerConf.of(
                    settingService.getDockerRegisterAddress(),
                    settingService.getDockerRegisterNamespace(),
                    settingService.getDockerRegisterUser(),
                    settingService.getDockerRegisterPassword()),
                app.getIngressTemplate());
        log.info("Submit params to building pipeline : {}", k8sApplicationBuildRequest);
        return FlinkK8sApplicationBuildPipeline.of(k8sApplicationBuildRequest);
      default:
        throw new UnsupportedOperationException(
            "Unsupported Building Application for ExecutionMode: " + app.getExecutionModeEnum());
    }
  }

  private void prepareJars(Application app) throws IOException {
    File localUploadDIR = new File(Workspace.local().APP_UPLOADS());
    if (!localUploadDIR.exists()) {
      localUploadDIR.mkdirs();
    }

    FsOperator localFS = FsOperator.lfs();
    // 1. copy jar to local upload dir
    if (app.isFlinkSqlJob() || app.isUploadJob()) {
      if (!app.getDependencyObject().getJar().isEmpty()) {
        for (String jar : app.getDependencyObject().getJar()) {
          File localJar = new File(WebUtils.getAppTempDir(), jar);
          File localUploadJar = new File(localUploadDIR, jar);
          if (!localJar.exists() && !localUploadJar.exists()) {
            throw new ApiAlertException("Missing file: " + jar + ", please upload again");
          }
          if (localJar.exists()) {
            checkOrElseUploadJar(localFS, localJar, localUploadJar, localUploadDIR);
          }
        }
      }
    }

    if (app.isCustomCodeJob()) {
      // customCode upload jar to appHome...
      FsOperator fsOperator = app.getFsOperator();

      if (app.isUploadJob()) {
        // 1). upload jar to local uploadDIR.
        File localJar = new File(WebUtils.getAppTempDir(), app.getJar());
        File localUploadJar = new File(localUploadDIR, app.getJar());
        checkOrElseUploadJar(localFS, localJar, localUploadJar, localUploadDIR);

        // 2) copy jar to local $app_home/lib
        boolean cleanUpload = false;
        File libJar = new File(app.getLocalAppLib(), app.getJar());
        if (!localFS.exists(app.getLocalAppLib())) {
          cleanUpload = true;
        } else {
          if (libJar.exists()) {
            if (!FileUtils.equals(localJar, libJar)) {
              cleanUpload = true;
            }
          } else {
            cleanUpload = true;
          }
        }

        if (cleanUpload) {
          localFS.mkCleanDirs(app.getLocalAppLib());
          localFS.upload(localUploadJar.getAbsolutePath(), app.getLocalAppLib());
        }

        // 3) for YARNApplication mode
        if (app.getExecutionModeEnum() == ExecutionMode.YARN_APPLICATION) {
          List<File> jars = new ArrayList<>(0);

          // 1). user jar
          jars.add(libJar);

          // 2). jar dependency
          app.getDependencyObject()
              .getJar()
              .forEach(jar -> jars.add(new File(localUploadDIR, jar)));

          // 3). pom dependency
          if (!app.getDependencyInfo().mavenArts().isEmpty()) {
            jars.addAll(MavenTool.resolveArtifactsAsJava(app.getDependencyInfo().mavenArts()));
          }

          // 4). local uploadDIR to hdfs uploadsDIR
          String hdfsUploadDIR = Workspace.remote().APP_UPLOADS();
          for (File jarFile : jars) {
            String hdfsUploadPath = hdfsUploadDIR + "/" + jarFile.getName();
            if (!fsOperator.exists(hdfsUploadPath)) {
              fsOperator.upload(jarFile.getAbsolutePath(), hdfsUploadDIR);
            } else {
              InputStream inputStream = Files.newInputStream(jarFile.toPath());
              if (!DigestUtils.md5Hex(inputStream).equals(fsOperator.fileMd5(hdfsUploadPath))) {
                fsOperator.upload(jarFile.getAbsolutePath(), hdfsUploadDIR);
              }
            }
          }

          // 5). copy jars to $hdfs_app_home/lib
          fsOperator.mkCleanDirs(app.getAppLib());
          jars.forEach(
              jar -> fsOperator.copy(hdfsUploadDIR + "/" + jar.getName(), app.getAppLib()));
        }
      } else {
        String appHome = app.getAppHome();
        fsOperator.mkCleanDirs(appHome);
        fsOperator.upload(app.getDistHome(), appHome);
      }
    }
  }

  /** copy from {@link ApplicationServiceImpl#start(Application, boolean)} */
  private String retrieveUserLocalJar(FlinkEnv flinkEnv, Application app) {
    switch (app.getDevelopmentMode()) {
      case CUSTOM_CODE:
        switch (app.getApplicationType()) {
          case STREAMPARK_FLINK:
            return String.format("%s/%s", app.getLocalAppLib(), app.getModule().concat(".jar"));
          case APACHE_FLINK:
            return String.format("%s/%s", app.getLocalAppLib(), app.getJar());
          default:
            throw new IllegalArgumentException(
                "[StreamPark] unsupported ApplicationType of custom code: "
                    + app.getApplicationType());
        }
      case FLINK_SQL:
        String sqlDistJar = commonService.getSqlClientJar(flinkEnv);
        return Workspace.local().APP_CLIENT().concat("/").concat(sqlDistJar);
      default:
        throw new UnsupportedOperationException(
            "[StreamPark] unsupported JobType: " + app.getDevelopmentMode());
    }
  }

  @Override
  public Optional<AppBuildPipeline> getCurrentBuildPipeline(@Nonnull Long appId) {
    return Optional.ofNullable(getById(appId));
  }

  @Override
  public DockerResolvedSnapshot getDockerProgressDetailSnapshot(@Nonnull Long appId) {
    return new DockerResolvedSnapshot(
        DOCKER_PULL_PG_SNAPSHOTS.getIfPresent(appId),
        DOCKER_BUILD_PG_SNAPSHOTS.getIfPresent(appId),
        DOCKER_PUSH_PG_SNAPSHOTS.getIfPresent(appId));
  }

  @Override
  public boolean allowToBuildNow(@Nonnull Long appId) {
    return getCurrentBuildPipeline(appId)
        .map(pipeline -> PipelineStatus.running != pipeline.getPipelineStatus())
        .orElse(true);
  }

  @Override
  public Map<Long, PipelineStatus> listPipelineStatus(List<Long> appIds) {
    if (CollectionUtils.isEmpty(appIds)) {
      return Collections.emptyMap();
    }
    LambdaQueryWrapper<AppBuildPipeline> queryWrapper =
        new LambdaQueryWrapper<AppBuildPipeline>().in(AppBuildPipeline::getAppId, appIds);

    List<AppBuildPipeline> appBuildPipelines = baseMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(appBuildPipelines)) {
      return Collections.emptyMap();
    }
    return appBuildPipelines.stream()
        .collect(Collectors.toMap(AppBuildPipeline::getAppId, AppBuildPipeline::getPipelineStatus));
  }

  @Override
  public void removeApp(Long appId) {
    baseMapper.delete(
        new LambdaQueryWrapper<AppBuildPipeline>().eq(AppBuildPipeline::getAppId, appId));
  }

  public boolean saveEntity(AppBuildPipeline pipe) {
    AppBuildPipeline old = getById(pipe.getAppId());
    if (old == null) {
      return save(pipe);
    } else {
      return updateById(pipe);
    }
  }

  private void checkOrElseUploadJar(
      FsOperator fsOperator, File localJar, File targetJar, File targetDir) {
    if (!fsOperator.exists(targetJar.getAbsolutePath())) {
      fsOperator.upload(localJar.getAbsolutePath(), targetDir.getAbsolutePath());
    } else {
      // The file exists to check whether it is consistent, and if it is inconsistent, re-upload it
      if (!FileUtils.equals(localJar, targetJar)) {
        fsOperator.upload(localJar.getAbsolutePath(), targetDir.getAbsolutePath());
      }
    }
  }
}
