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

package org.apache.streampark.flink.kubernetes.watcher;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class FlinkWatcher implements AutoCloseable {

  private final AtomicBoolean STARTED = new AtomicBoolean(false);

  public void start() {
    synchronized (this) {
      if (!STARTED.getAndSet(true)) {
        doStart();
      }
    }
  }

  public void stop() {
    synchronized (this) {
      if (STARTED.getAndSet(false)) {
        doStop();
      }
    }
  }

  @Override
  public void close() throws Exception {
    if (STARTED.get()) {
      doStop();
    }
    doClose();
  }

  public void restart() {
    synchronized (this) {
      stop();
      start();
    }
  }

  abstract void doStart();

  abstract void doStop();

  abstract void doClose();

  abstract void doWatch();
}
