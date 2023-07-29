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

package org.apache.streampark.gateway.flink.v2;

import org.apache.streampark.gateway.CompleteStatementRequestBody;
import org.apache.streampark.gateway.ExecutionConfiguration;
import org.apache.streampark.gateway.OperationHandle;
import org.apache.streampark.gateway.OperationStatus;
import org.apache.streampark.gateway.exception.SqlGatewayException;
import org.apache.streampark.gateway.flink.v2.client.dto.ColumnInfo;
import org.apache.streampark.gateway.flink.v2.client.dto.ExecuteStatementRequestBody;
import org.apache.streampark.gateway.flink.v2.client.dto.FetchResultsResponseBody;
import org.apache.streampark.gateway.flink.v2.client.dto.GetInfoResponseBody;
import org.apache.streampark.gateway.flink.v2.client.dto.OpenSessionRequestBody;
import org.apache.streampark.gateway.flink.v2.client.dto.OperationStatusResponseBody;
import org.apache.streampark.gateway.flink.v2.client.dto.ResultType;
import org.apache.streampark.gateway.flink.v2.client.dto.RowFormat;
import org.apache.streampark.gateway.flink.v2.client.rest.ApiClient;
import org.apache.streampark.gateway.flink.v2.client.rest.ApiException;
import org.apache.streampark.gateway.flink.v2.client.rest.DefaultApi;
import org.apache.streampark.gateway.results.Column;
import org.apache.streampark.gateway.results.GatewayInfo;
import org.apache.streampark.gateway.results.OperationInfo;
import org.apache.streampark.gateway.results.ResultKind;
import org.apache.streampark.gateway.results.ResultQueryCondition;
import org.apache.streampark.gateway.results.ResultSet;
import org.apache.streampark.gateway.results.RowData;
import org.apache.streampark.gateway.service.SqlGatewayService;
import org.apache.streampark.gateway.session.SessionEnvironment;
import org.apache.streampark.gateway.session.SessionHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Implement {@link SqlGatewayService} with Flink native SqlGateway. */
public class FlinkSqlGatewayImpl implements SqlGatewayService {

  private final DefaultApi defaultApi;

  public FlinkSqlGatewayImpl(String baseUri) {
    ApiClient client = new ApiClient();
    client.setBasePath(baseUri);
    defaultApi = new DefaultApi(client);
  }

  @Override
  public boolean check(String flinkMajorVersion) {
    // flink gateway v1 api is supported from flink 1.16
    return Double.parseDouble(flinkMajorVersion) >= 1.16;
  }

  @Override
  public GatewayInfo getGatewayInfo() throws SqlGatewayException {
    GetInfoResponseBody info = null;
    try {
      info = defaultApi.getInfo();
      return new GatewayInfo(info.getProductName(), info.getVersion());
    } catch (ApiException e) {
      throw new SqlGatewayException("Flink native SqlGateWay getGatewayInfo failed!", e);
    }
  }

  @Override
  public SessionHandle openSession(SessionEnvironment environment) throws SqlGatewayException {
    try {
      return new SessionHandle(
          Objects.requireNonNull(
              defaultApi
                  .openSession(
                      new OpenSessionRequestBody()
                          .sessionName(environment.getSessionName())
                          .properties(environment.getSessionConfig()))
                  .getSessionHandle()));
    } catch (ApiException e) {
      throw new SqlGatewayException("Flink native SqlGateWay openSession failed!", e);
    }
  }

  @Override
  public void heartbeat(SessionHandle sessionHandle) throws SqlGatewayException {
    try {
      defaultApi.triggerSession(sessionHandle.getIdentifier());
    } catch (ApiException e) {
      throw new SqlGatewayException("Flink native SqlGateWay heartbeat failed!", e);
    }
  }

  @Override
  public void closeSession(SessionHandle sessionHandle) throws SqlGatewayException {
    try {
      defaultApi.closeSession(sessionHandle.getIdentifier());
    } catch (ApiException e) {
      throw new SqlGatewayException("Flink native SqlGateWay closeSession failed!", e);
    }
  }

  @Override
  public void cancelOperation(SessionHandle sessionHandle, OperationHandle operationHandle)
      throws SqlGatewayException {
    try {
      defaultApi.cancelOperation(sessionHandle.getIdentifier(), operationHandle.getIdentifier());
    } catch (ApiException e) {
      throw new SqlGatewayException("Flink native SqlGateWay cancelOperation failed!", e);
    }
  }

  @Override
  public void closeOperation(SessionHandle sessionHandle, OperationHandle operationHandle)
      throws SqlGatewayException {
    try {
      defaultApi.closeOperation(sessionHandle.getIdentifier(), operationHandle.getIdentifier());
    } catch (ApiException e) {
      throw new SqlGatewayException("Flink native SqlGateWay closeOperation failed!", e);
    }
  }

  @Override
  public OperationInfo getOperationInfo(
      SessionHandle sessionHandle, OperationHandle operationHandle) throws SqlGatewayException {

    try {
      OperationStatusResponseBody operationStatus =
          defaultApi.getOperationStatus(
              sessionHandle.getIdentifier(), operationHandle.getIdentifier());
      return new OperationInfo(OperationStatus.valueOf(operationStatus.getStatus()), null);
    } catch (ApiException e) {
      throw new SqlGatewayException("Flink native SqlGateWay closeOperation failed!", e);
    }
  }

  @Override
  public Column getOperationResultSchema(
      SessionHandle sessionHandle, OperationHandle operationHandle) throws SqlGatewayException {
    throw new SqlGatewayException(
        "Flink native SqlGateWay don`t support operation:getOperationResultSchema!");
  }

  @Override
  public OperationHandle executeStatement(
      SessionHandle sessionHandle,
      String statement,
      long executionTimeoutMs,
      ExecutionConfiguration executionConfig)
      throws SqlGatewayException {
    try {
      return new OperationHandle(
          Objects.requireNonNull(
              defaultApi
                  .executeStatement(
                      sessionHandle.getIdentifier(),
                      new ExecuteStatementRequestBody()
                          .statement(statement)
                          // currently, sql gateway don't support execution timeout
                          //              .executionTimeout(executionTimeoutMs)
                          .putExecutionConfigItem(
                              "pipeline.name", "Flink SQL Gateway SDK on flink cluster Example"))
                  .getOperationHandle()));
    } catch (ApiException e) {
      throw new SqlGatewayException("Flink native SqlGateWay executeStatement failed!", e);
    }
  }

  @Override
  public ResultSet fetchResults(
      SessionHandle sessionHandle,
      OperationHandle operationHandle,
      ResultQueryCondition resultQueryCondition)
      throws SqlGatewayException {
    try {

      List<RowData> data = new ArrayList<>();
      List<Column> columns = new ArrayList<>();
      FetchResultsResponseBody fetchResultsResponseBody =
          defaultApi.fetchResults(
              sessionHandle.getIdentifier(),
              operationHandle.getIdentifier(),
              resultQueryCondition.getToken(),
              RowFormat.JSON);
      ResultType resultType = fetchResultsResponseBody.getResultType();
      Long nextToken = null;
      if (fetchResultsResponseBody.getNextResultUri() != null) {
        String nextResultUri = fetchResultsResponseBody.getNextResultUri();
        nextToken =
            Long.valueOf(
                nextResultUri.substring(
                    nextResultUri.lastIndexOf("/") + 1, nextResultUri.lastIndexOf("?")));
      }

      org.apache.streampark.gateway.flink.v2.client.dto.ResultInfo results =
          fetchResultsResponseBody.getResults();

      List<ColumnInfo> resultsColumns = results.getColumns();
      List<org.apache.streampark.gateway.flink.v2.client.dto.RowData> resultsData =
          results.getData();

      resultsColumns.forEach(
          column ->
              columns.add(
                  new Column(
                      column.getName(), column.getLogicalType().toString(), column.getComment())));
      resultsData.forEach(row -> data.add(new RowData(row.getKind().getValue(), row.getFields())));

      ResultKind resultKind =
          columns.size() == 1 && columns.get(0).getName().equals("result")
              ? ResultKind.SUCCESS
              : ResultKind.SUCCESS_WITH_CONTENT;

      return new ResultSet(
          ResultSet.ResultType.valueOf(resultType.getValue()),
          nextToken,
          columns,
          data,
          true,
          null,
          resultKind);
    } catch (ApiException e) {
      throw new SqlGatewayException("Flink native SqlGateWay fetchResults failed!", e);
    }
  }

  @Override
  public List<String> completeStatement(
      SessionHandle sessionHandle, CompleteStatementRequestBody completeStatementRequestBody)
      throws SqlGatewayException {

    try {
      return defaultApi
          .completeStatement(
              sessionHandle.getIdentifier(),
              new org.apache.streampark.gateway.flink.v2.client.dto.CompleteStatementRequestBody()
                  .statement(completeStatementRequestBody.getStatement())
                  .position(completeStatementRequestBody.getPosition()))
          .getCandidates();
    } catch (ApiException e) {
      throw new SqlGatewayException("Flink native SqlGateWay completeStatement failed!", e);
    }
  }
}
