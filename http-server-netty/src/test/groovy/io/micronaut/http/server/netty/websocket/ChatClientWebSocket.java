/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.http.server.netty.websocket;

// tag::imports[]

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.HttpRequest;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.ClientWebSocket;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import org.reactivestreams.Publisher;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

// tag::class[]
@Requires(property = "spec.name", value = "SimpleTextWebSocketSpec")
@ClientWebSocket("/chat/{topic}/{username}") // <1>
public abstract class ChatClientWebSocket implements AutoCloseable { // <2>

    private WebSocketSession session;
    private HttpRequest request;
    private String topic;
    private String username;
    private Collection<String> replies = new ConcurrentLinkedQueue<>();
    private String subProtocol;

    @OnOpen
    public void onOpen(String topic, String username, WebSocketSession session, HttpRequest request) { // <3>
        this.topic = topic;
        this.username = username;
        this.session = session;
        this.request = request;
        this.subProtocol = session.getSubprotocol().orElse(null);
    }

    public String getTopic() {
        return topic;
    }

    public String getUsername() {
        return username;
    }

    public Collection<String> getReplies() {
        return replies;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public HttpRequest getRequest() {
        return request;
    }

    @OnMessage
    public void onMessage(
            String message) {
        replies.add(message); // <4>
    }

// end::class[]
    public abstract void send(String message);

    public abstract Future<String> sendAsync(String message);

    @SingleResult
    public abstract Publisher<String> sendRx(String message);

    public String getSubProtocol() {
        return subProtocol;
    }
}
