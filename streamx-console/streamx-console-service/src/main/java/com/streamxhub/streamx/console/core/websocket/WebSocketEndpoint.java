/*
 * Copyright (c) 2019 The StreamX Project
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.streamxhub.streamx.console.core.websocket;

import io.undertow.util.CopyOnWriteMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;

/**
 * @author benjobs
 */
@Slf4j
@Component
@ServerEndpoint(value = "/websocket/{id}")
public class WebSocketEndpoint {

    public static Map<String, Session> socketSessions = new CopyOnWriteMap<>();

    @Getter
    private String id;

    @Getter
    private Session session;

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        log.info("websocket onOpen....");
        this.id = id;
        this.session = session;
        socketSessions.put(id, session);
    }

    @OnClose
    public void onClose() throws IOException {
        log.info("websocket onClose....");
        this.session.close();
        socketSessions.remove(this.id);
    }

    @OnError
    public void onError(Session session, Throwable e) {
        e.printStackTrace();
    }

    public static void writeMessage(String socketId, String message) {
        try {
            Session session = socketSessions.get(socketId);
            if (session != null) {
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

