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
import { BasicColumn, FormSchema } from '/@/components/Table';
import { h } from 'vue';
import { Tag } from 'ant-design-vue';
import { checkUserName, fetchUserTypes } from '/@/api/system/user';
import { FormTypeEnum } from '/@/enums/formEnum';
import { useI18n } from '/@/hooks/web/useI18n';
const { t } = useI18n();
// user status enum
const enum StatusEnum {
  Effective = '1',
  Locked = '0',
}

// gender
const enum GenderEnum {
  Male = '0',
  Female = '1',
  Other = '2',
}

export const columns: BasicColumn[] = [
  { title: t('system.user.form.userName'), dataIndex: 'username', sorter: true },
  { title: t('system.user.form.nickName'), dataIndex: 'nickName' },
  { title: t('system.user.form.userType'), dataIndex: 'userType' },
  {
    title: t('system.user.form.status'),
    dataIndex: 'status',
    customRender: ({ record }) => {
      const enable = record?.status === StatusEnum.Effective;
      const color = enable ? 'green' : 'red';
      const text = enable ? 'Effective' : 'locked';
      return h(Tag, { color }, () => text);
    },
    filters: [
      { text: 'Effective', value: '1' },
      { text: 'Locked', value: '0' },
    ],
    filterMultiple: false,
  },
  {
    title: t('common.createTime'),
    dataIndex: 'createTime',
    sorter: true,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'username',
    label: t('system.user.form.userName'),
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    label: t('common.createTime'),
    field: 'createTime',
    component: 'RangePicker',
    colProps: { span: 8 },
  },
];

export const formSchema = (formType: string): FormSchema[] => {
  const isCreate = formType === FormTypeEnum.Create;
  // const isUpdate = formType === FormTypeEnum.Edit;
  const isView = formType === FormTypeEnum.View;

  return [
    { field: 'userId', label: 'User Id', component: 'Input', show: false },
    {
      field: 'username',
      label: t('system.user.form.userName'),
      component: 'Input',
      rules: [
        { required: isCreate, message: t('system.user.form.required') },
        { min: 4, message: t('system.user.form.min') },
        { max: 8, message: t('system.user.form.max') },
        {
          validator: async (_, value) => {
            if (!isCreate || !value || value.length < 4 || value.length > 8) {
              return Promise.resolve();
            }
            const res = await checkUserName({ username: value });
            if (!res) {
              return Promise.reject(t('system.user.form.exist'));
            }
          },
          trigger: 'blur',
        },
      ],
      componentProps: {
        id: 'formUserName',
        disabled: !isCreate,
      },
    },
    {
      field: 'nickName',
      label: t('system.user.form.nickName'),
      component: 'Input',
      dynamicRules: () => {
        return [{ required: isCreate, message: 'nickName is required' }];
      },
      componentProps: { disabled: !isCreate },
    },
    {
      field: 'password',
      label: t('system.user.form.password'),
      component: 'InputPassword',
      componentProps: { placeholder: 'please enter password' },
      helpMessage: t('system.user.form.passwordHelp'),
      rules: [
        { required: true, message: t('system.user.form.passwordRequire') },
        { min: 8, message: t('system.user.form.passwordHelp') },
      ],
      required: true,
      ifShow: isCreate,
    },
    {
      field: 'email',
      label: 'E-Mail',
      component: 'Input',
      rules: [
        { type: 'email', message: t('system.user.form.email') },
        { max: 50, message: t('system.user.form.maxEmail') },
      ],
      componentProps: {
        readonly: isView,
        placeholder: 'please enter email',
      },
    },
    {
      label: t('system.user.form.userType'),
      field: 'userType',
      component: 'ApiSelect',
      componentProps: {
        disabled: isView,
        api: fetchUserTypes,
        placeholder: 'Please select a user type',
      },
      rules: [{ required: true, message: 'Please select a user type' }],
    },
    {
      field: 'status',
      label: t('system.user.form.status'),
      component: 'RadioGroup',
      defaultValue: StatusEnum.Effective,
      componentProps: {
        options: [
          { label: 'locked', value: StatusEnum.Locked },
          { label: 'effective', value: StatusEnum.Effective },
        ],
      },
      rules: [{ required: true, message: 'please select status' }],
    },
    {
      field: 'sex',
      label: t('system.user.form.gender'),
      component: 'RadioGroup',
      defaultValue: GenderEnum.Male,
      componentProps: {
        options: [
          { label: 'male', value: GenderEnum.Male },
          { label: 'female', value: GenderEnum.Female },
          { label: 'secret', value: GenderEnum.Other },
        ],
      },
      required: true,
    },
    {
      field: 'description',
      label: t('common.description'),
      component: 'InputTextArea',
      componentProps: { rows: 5, placeholder: 'Please enter description' },
      ifShow: isCreate,
    },
  ];
};
