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
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { handleIsStart } from '../utils';
import { useFlinkAppStore } from '/@/store/modules/flinkApplication';
import { useFlinkApplication } from './useApp';
import { fetchAppRecord, fetchAppRemove } from '/@/api/flink/app/app';
import { AppListRecord } from '/@/api/flink/app/app.type';
import { ActionItem, FormProps } from '/@/components/Table';
import { useMessage } from '/@/hooks/web/useMessage';
import {
  AppStateEnum,
  AppTypeEnum,
  ExecModeEnum,
  JobTypeEnum,
  OptionStateEnum,
  ReleaseStateEnum,
} from '/@/enums/flinkEnum';
import { usePermission } from '/@/hooks/web/usePermission';
import { useI18n } from '/@/hooks/web/useI18n';
import { isFunction, isObject } from '/@/utils/is';

export const useAppTableAction = (
  openStartModal: Fn,
  openStopModal: Fn,
  openSavepointModal: Fn,
  openLogModal: Fn,
  openBuildDrawer: Fn,
  handlePageDataReload: Fn,
  optionApps: Recordable,
  openAlertModal: Fn
) => {
  const { t } = useI18n();
  const tagsOptions = ref<Recordable>([]);

  const flinkAppStore = useFlinkAppStore();
  const router = useRouter();
  const { createMessage } = useMessage();
  const { hasPermission } = usePermission();
  const {
    handleCheckReleaseApp,
    handleAppCheckStart,
    handleCanStop,
    handleForcedStop,
    handleCopy,
    handleMapping,
    users,
  } = useFlinkApplication(openStartModal);
  /* Operation button list */
  // 操作列表
  function getActionList(record: AppListRecord, currentPageNo: number): ActionItem[] {
    return [
      {// 发布作业
        tooltip: { title: t('flink.app.operation.release') },
        ifShow:
          [
            ReleaseStateEnum.FAILED,
            ReleaseStateEnum.NEED_RELEASE,
            ReleaseStateEnum.NEED_ROLLBACK,
          ].includes(record.release) && record['optionState'] == OptionStateEnum.NONE,
        auth: 'app:release',
        icon: 'ant-design:cloud-upload-outlined',
        onClick: handleCheckReleaseApp.bind(null, record),
      },
      {// 发布详情
        tooltip: { title: t('flink.app.operation.releaseDetail') },
        ifShow:
          [ReleaseStateEnum.FAILED, ReleaseStateEnum.RELEASING].includes(record.release) ||
          record['optionState'] == OptionStateEnum.RELEASING,
        auth: 'app:release',
        icon: 'ant-design:container-outlined',
        onClick: () => openBuildDrawer(true, { appId: record.id }),
      },
      {// 启动作业
        tooltip: { title: t('flink.app.operation.start') },
        ifShow: handleIsStart(record, optionApps),
        auth: 'app:start',
        icon: 'ant-design:play-circle-outlined',
        onClick: handleAppCheckStart.bind(null, record),
      },
      {// 取消作业
        tooltip: { title: t('flink.app.operation.cancel') },
        ifShow:
          record.state == AppStateEnum.RUNNING && record['optionState'] == OptionStateEnum.NONE,
        auth: 'app:cancel',
        icon: 'ant-design:pause-circle-outlined',
        onClick: handleCancel.bind(null, record),
      },
      {// 编辑作业
        tooltip: { title: t('flink.app.operation.edit') },
        auth: 'app:update',
        icon: 'clarity:note-edit-line',
        onClick: handleEdit.bind(null, record, currentPageNo),
      },
      {// 作业详情
        tooltip: { title: t('flink.app.operation.detail') },
        auth: 'app:detail',
        icon: 'carbon:data-view-alt',
        onClick: handleDetail.bind(null, record),
      },
      {// 查看 Flink 启动日志
        tooltip: { title: t('flink.app.operation.startLog') },
        ifShow: [ExecModeEnum.KUBERNETES_SESSION, ExecModeEnum.KUBERNETES_APPLICATION].includes(
          record.executionMode,
        ),
        auth: 'app:detail',
        icon: 'ant-design:code-outlined',
        onClick: () => openLogModal(true, { app: record }),
      },
      {// 触发 Savepoint
        tooltip: { title: t('flink.app.operation.savepoint') },
        ifShow:
          record.state == AppStateEnum.RUNNING && record['optionState'] == OptionStateEnum.NONE,
        auth: 'savepoint:trigger',
        icon: 'ant-design:camera-outlined',
        onClick: handleSavepoint.bind(null, record),
      },
      {// 强制停止作业
        tooltip: { title: t('flink.app.operation.force') },
        ifShow: handleCanStop(record),
        auth: 'app:cancel',
        icon: 'ant-design:pause-circle-outlined',
        onClick: handleForcedStop.bind(null, record),
      },
      {// 复制作业
        label: t('flink.app.operation.copy'),
        auth: 'app:copy',
        icon: 'ant-design:copy-outlined',
        onClick: handleCopy.bind(null, record),
      },
      {// 重新映射作业
        label: t('flink.app.operation.remapping'),
        ifShow: [
          AppStateEnum.ADDED,
          AppStateEnum.FAILED,
          AppStateEnum.CANCELED,
          AppStateEnum.KILLED,
          AppStateEnum.SUCCEEDED,
          AppStateEnum.TERMINATED,
          AppStateEnum.POS_TERMINATED,
          AppStateEnum.FINISHED,
          AppStateEnum.SUSPENDED,
          AppStateEnum.LOST,
        ].includes(record.state),
        auth: 'app:mapping',
        icon: 'ant-design:deployment-unit-outlined',
        onClick: handleMapping.bind(null, record),
      },
      {// 删除
        popConfirm: {
          title: t('flink.app.operation.deleteTip'),
          confirm: handleDelete.bind(null, record),
        },
        label: t('common.delText'),
        ifShow: [
          AppStateEnum.ADDED,
          AppStateEnum.FAILED,
          AppStateEnum.CANCELED,
          AppStateEnum.FINISHED,
          AppStateEnum.LOST,
          AppStateEnum.TERMINATED,
          AppStateEnum.POS_TERMINATED,
          AppStateEnum.SUCCEEDED,
          AppStateEnum.KILLED,
        ].includes(record.state),
        auth: 'app:delete',
        icon: 'ant-design:delete-outlined',
        color: 'error',
      },
    ];
  }
  /** action button is show */
  function actionIsShow(tableActionItem: ActionItem): boolean | undefined {
    const { auth, ifShow } = tableActionItem;
    let flag = isFunction(ifShow) ? ifShow(tableActionItem) : ifShow;
    /** Judgment auth when not set or allowed to display */
    if ((flag || flag === undefined) && auth) flag = hasPermission(auth);
    return flag;
  }

  function getTableActions(
    record: AppListRecord,
    currentPageNo: any,
  ): { actions: ActionItem[]; dropDownActions: ActionItem[] } {
    const tableAction = getActionList(record, currentPageNo).filter((item: ActionItem) =>
      actionIsShow(item),
    );
    // console.log('record',record); 行数据
    // console.log(tableAction);
    const actions = tableAction.splice(0, 3).map((item: ActionItem) => {
      if (item.label) {
        item.tooltip = {
          title: item.label,
        };
        delete item.label;
      }
      return item;
    });
    // console.log(actions); 行操作
    return {
      actions,
      dropDownActions: tableAction.map((item: ActionItem) => {
        if (!item.label && isObject(item.tooltip)) item.label = item.tooltip?.title || '';
        return item;
      }),
    };
  }
  /* Click to edit */
  function handleEdit(app: AppListRecord, currentPageNo: number) {
    // Record the current page number
    console.log('当前行数据为', app);
    sessionStorage.setItem('appPageNo', String(currentPageNo || 1));
    // sessionStorage.setItem('editJobModalParams', JSON.stringify(app));
    flinkAppStore.setApplicationId(app.id);
    if (app.appType == AppTypeEnum.STREAMPARK_FLINK) {
      // jobType( 1 custom code 2: flinkSQL)
      router.push({ path: '/flink/app/edit_streampark', query: { appId: app.id } });
    } else if (app.appType == AppTypeEnum.APACHE_FLINK) {
      //Apache Flink
      router.push({ path: '/flink/app/edit_flink', query: { appId: app.id } });
    }
  }

  /* Click for details */
  function handleDetail(app: AppListRecord) {
    flinkAppStore.setApplicationId(app.id);
    router.push({ path: '/flink/app/detail', query: { appId: app.id } });
  }
  // click savepoint for application
  function handleSavepoint(app: AppListRecord) {
    if (!optionApps.savepointing.get(app.id) || app['optionState'] == OptionStateEnum.NONE) {
      openSavepointModal(app);
    }
  }
  // click stop application
  function handleCancel(app: AppListRecord) {
    if (!optionApps.stopping.get(app.id) || app['optionState'] == OptionStateEnum.NONE) {
      openStopModal(true, { application: app });
    }
  }

  /* Click to delete */
  async function handleDelete(app: AppListRecord) {
    const hide = createMessage.loading('deleting', 0);
    try {
      await fetchAppRemove(app.id);
      createMessage.success('delete successful');
      handlePageDataReload(false);
    } catch (error) {
      console.error(error);
    } finally {
      hide();
    }
  }

  const formConfig = computed((): Partial<FormProps> => {
    const tableFormConfig: FormProps = {
      baseColProps: { span: 4, style: { paddingRight: '30px' } },
      actionColOptions: { style: { paddingRight: '0px' } },
      showSubmitButton: false,
      showResetButton: false,
      async resetFunc() {
        // router.push({ path: '/flink/app/add' });
        // @newadd 0428
        // openAlertModal(true, {})
        openAlertModal()
        console.log('添加');
      },
      schemas: [
        {
          label: t('flink.app.searchName'),
          field: 'jobName',
          component: 'Input',
          componentProps: {
            placeholder: t('flink.app.searchName'),
            onChange: handlePageDataReload.bind(null, false),
            onSearch: handlePageDataReload.bind(null, false),
          },
        },
        {
          label: t('flink.app.tags'),
          field: 'tags',
          component: 'Select',
          componentProps: {
            placeholder: t('flink.app.tags'),
            showSearch: true,
            options: tagsOptions.value.map((t: Recordable) => ({ label: t, value: t })),
            onChange: handlePageDataReload.bind(null, false),
          },
        },
        {
          label: t('flink.app.owner'),
          field: 'userId',
          component: 'Select',
          componentProps: {
            placeholder: t('flink.app.owner'),
            showSearch: true,
            options: users.value.map((u: Recordable) => {
              return { label: u.nickName || u.username, value: u.userId };
            }),
            onChange: handlePageDataReload.bind(null, false),
          },
        },
        {
          label: t('flink.app.jobType'),
          field: 'jobType',
          component: 'Select',
          componentProps: {
            placeholder: t('flink.app.jobType'),
            showSearch: true,
            options: [
              { label: 'JAR', value: JobTypeEnum.JAR },
              { label: 'SQL', value: JobTypeEnum.SQL },
            ],
            onChange: handlePageDataReload.bind(null, false),
          },
        },
      ],
    };
    if (hasPermission('app:create')) {
      Object.assign(tableFormConfig, {
        showResetButton: true,
        resetButtonOptions: {
          text: t('common.add'),
          color: 'primary',
          preIcon: 'ant-design:plus-outlined',
          style: { float: 'right' },
        },
      });
    }
    return tableFormConfig;
  });

  /*  tag */
  function handleInitTagsOptions() {
    const params = Object.assign({}, { pageSize: 999999999, pageNum: 1 });
    fetchAppRecord(params).then((res) => {
      const dataSource = res?.records || [];
      dataSource.forEach((record) => {
        if (record.tags !== null && record.tags !== undefined && record.tags !== '') {
          const tagsArray = record.tags.split(',') as string[];
          tagsArray.forEach((x: string) => {
            if (x.length > 0 && tagsOptions.value.indexOf(x) == -1) {
              tagsOptions.value.push(x);
            }
          });
        }
      });
    });
  }
  onMounted(() => {
    handleInitTagsOptions();
  });
  return { getTableActions, formConfig };
};
