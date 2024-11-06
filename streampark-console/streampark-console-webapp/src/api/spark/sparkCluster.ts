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
import { AxiosResponse } from 'axios';
import { SparkCluster } from './sparkCluster.type';
import { Result } from '/#/axios';
import { defHttp } from '/@/utils/http/axios';
import type { BasicTableParams } from '../model/baseModel';

enum SPARK_API {
  PAGE = '/spark/cluster/page',
  LIST = '/spark/cluster/list',
  REMOTE_URL = '/spark/cluster/remote_url',
  CREATE = '/spark/cluster/create',
  CHECK = '/spark/cluster/check',
  GET = '/spark/cluster/get',
  UPDATE = '/spark/cluster/update',
  START = '/spark/cluster/start',
  SHUTDOWN = '/spark/cluster/shutdown',
  DELETE = '/spark/cluster/delete',
}
/**
 * spark cluster
 * @returns Promise<SparkEnv[]>
 */
export function fetchSparkClusterPage(data: BasicTableParams) {
  return defHttp.post<SparkCluster[]>({
    url: SPARK_API.PAGE,
    data,
  });
}
/**
 * spark cluster
 * @returns Promise<SparkEnv[]>
 */
export function fetchSparkCluster() {
  return defHttp.post<SparkCluster[]>({
    url: SPARK_API.LIST,
  });
}
/**
 * spark cluster start
 * @returns {Promise<AxiosResponse<Result>>}
 */
export function fetchClusterStart(id: string): Promise<AxiosResponse<Result>> {
  return defHttp.post({ url: SPARK_API.START, data: { id } }, { isReturnNativeResponse: true });
}
/**
 * spark cluster remove
 * @returns {Promise<AxiosResponse<Result>>}
 */
export function fetchClusterRemove(id: string): Promise<AxiosResponse<Result>> {
  return defHttp.post({ url: SPARK_API.DELETE, data: { id } }, { isReturnNativeResponse: true });
}
/**
 * spark cluster shutdown
 * @returns  {Promise<AxiosResponse<Result>>}
 */
export function fetchClusterShutdown(id: string): Promise<AxiosResponse<Result>> {
  return defHttp.post<AxiosResponse<Result>>(
    { url: SPARK_API.SHUTDOWN, data: { id } },
    { isReturnNativeResponse: true },
  );
}
/**
 * spark cluster shutdown
 * @returns {Promise<string>}
 */
export function fetchRemoteURL(id: string): Promise<string> {
  return defHttp.post<string>({
    url: SPARK_API.REMOTE_URL,
    data: { id },
  });
}

export function fetchCheckCluster(data: Recordable) {
  return defHttp.post({
    url: SPARK_API.CHECK,
    data,
  });
}

export function fetchCreateCluster(data: Recordable) {
  return defHttp.post({
    url: SPARK_API.CREATE,
    data,
  });
}
export function fetchUpdateCluster(data: Recordable) {
  return defHttp.post({
    url: SPARK_API.UPDATE,
    data,
  });
}

export function fetchGetCluster(data: Recordable) {
  return defHttp.post({
    url: SPARK_API.GET,
    data,
  });
}
