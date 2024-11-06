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

package org.apache.streampark.console.core.metrics.spark;

import lombok.Getter;

import java.util.List;

public class SparkApplicationMetrics {

    public String url;
    public int aliveworkers;
    public int cores;
    public int coresused;
    public int memory;
    public int memoryused;
    public List<Application> activeapps;
    public List<Application> completedapps;

    public String status;

    public boolean isRunning() {
        return status.equals("ALIVE");
    }

    @Getter
    public static class Application {

        public String id;
        public long starttime;
        public String name;
        public int cores;
        public String user;
        public int memoryperexecutor;
        public int memoryperslave;
        public String submitdate;
        public String state;
        public long duration;

        public boolean matchId(String id) {
            return this.id.equals(id);
        }
    }

}
