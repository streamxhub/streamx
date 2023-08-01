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

package org.apache.streampark.gateway;

import java.io.Serializable;
import java.util.Objects;

public class CompleteStatementRequestBody implements Serializable {

  private Integer position;

  private String statement;

  public CompleteStatementRequestBody() {}

  public CompleteStatementRequestBody(Integer position, String statement) {
    this.position = position;
    this.statement = statement;
  }

  public Integer getPosition() {
    return position;
  }

  public String getStatement() {
    return statement;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompleteStatementRequestBody that = (CompleteStatementRequestBody) o;
    return Objects.equals(position, that.position) && Objects.equals(statement, that.statement);
  }

  @Override
  public int hashCode() {
    return Objects.hash(position, statement);
  }

  @Override
  public String toString() {
    return "CompleteStatementRequestBody{"
        + "position="
        + position
        + ", statement='"
        + statement
        + '\''
        + '}';
  }
}
