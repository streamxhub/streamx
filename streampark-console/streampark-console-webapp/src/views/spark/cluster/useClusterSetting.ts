import { DeployMode } from '/@/enums/sparkEnum';
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
import { RuleObject } from 'ant-design-vue/lib/form';
import { StoreValue } from 'ant-design-vue/lib/form/interface';
import { computed, onMounted, ref, unref } from 'vue';
import { FormSchema } from '/@/components/Table';
import { useMessage } from '/@/hooks/web/useMessage';
import { useI18n } from '/@/hooks/web/useI18n';
import { AlertSetting } from '/@/api/setting/types/alert.type';
import { fetchSparkEnvList } from '/@/api/spark/home';
import { SparkEnv } from '/@/api/spark/home.type';

export const useClusterSetting = () => {
  const { createMessage } = useMessage();
  const { t } = useI18n();

  const submitLoading = ref(false);
  const sparkEnvs = ref<SparkEnv[]>([]);
  const alerts = ref<AlertSetting[]>([]);

  const changeLoading = (loading: boolean) => {
    submitLoading.value = loading;
  };
  const getLoading = computed(() => submitLoading.value);

  /* check */
  async function handleCheckDeployMode(_rule: RuleObject, value: StoreValue) {
    if (value === null || value === undefined || value === '') {
      return Promise.reject(t('setting.sparkCluster.required.deployMode'));
    } else {
    }
  }

  const getClusterSchema = computed((): FormSchema[] => {
    return [
      {
        field: 'clusterName',
        label: t('setting.sparkCluster.form.clusterName'),
        component: 'Input',
        componentProps: {
          placeholder: t('setting.sparkCluster.placeholder.clusterName'),
        },
        required: true,
      },
      {
        field: 'deployMode',
        label: t('setting.sparkCluster.form.deployMode'),
        component: 'Select',
        componentProps: {
          placeholder: t('setting.sparkCluster.placeholder.deployMode'),
          options: [
            {
              label: 'remote',
              value: DeployMode.REMOTE,
            },
          ],
        },
        dynamicRules: () => {
          return [{ required: true, validator: handleCheckDeployMode }];
        },
      },
      {
        field: 'versionId',
        label: t('setting.sparkCluster.form.versionId'),
        component: 'Select',
        componentProps: {
          placeholder: t('setting.sparkCluster.placeholder.versionId'),
          options: unref(sparkEnvs),
          fieldNames: { label: 'sparkName', value: 'id', options: 'options' },
        },
        rules: [{ required: true, message: t('setting.sparkCluster.required.versionId') }],
      },
      {
        field: 'masterWebUrl',
        label: 'master web URL',
        component: 'Input',
        componentProps: {
          placeholder: t('setting.sparkCluster.placeholder.masterWebUrl'),
        },
        ifShow: ({ values }) => values.deployMode == DeployMode.REMOTE,
        rules: [{ required: true, message: t('setting.sparkCluster.required.masterWebUrl') }],
      },
      {
        field: 'alertId',
        label: t('flink.app.faultAlertTemplate'),
        component: 'Select',
        componentProps: {
          placeholder: t('flink.app.addAppTips.alertTemplatePlaceholder'),
          options: unref(alerts),
          fieldNames: { label: 'alertName', value: 'id', options: 'options' },
        },
        ifShow: ({ values }) => values.deployMode == DeployMode.REMOTE,
      },
    ];
  });
  function handleSubmitParams(values: Recordable) {
    const params = {
      clusterName: values.clusterName,
      deployMode: values.deployMode,
      versionId: values.versionId,
      description: values.description,
      alertId: values.alertId,
    };

    switch (values.deployMode) {
      case DeployMode.REMOTE:
        Object.assign(params, {
          masterWebUrl: values.masterWebUrl,
        });
        return params;
      default:
        createMessage.error('error deployMode.');
        return {};
    }
  }
  onMounted(() => {
    //get flinkEnv
    fetchSparkEnvList().then((res) => {
      sparkEnvs.value = res;
    });
  });
  return { getClusterSchema, handleSubmitParams, changeLoading, getLoading };
};
