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
package io.micronaut.websocket.annotation;

import io.micronaut.aop.Introduction;
import io.micronaut.context.annotation.AliasFor;
import io.micronaut.context.annotation.DefaultScope;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.context.annotation.Type;
import io.micronaut.websocket.WebSocketVersion;
import io.micronaut.websocket.interceptor.ClientWebSocketInterceptor;
import io.micronaut.websocket.interceptor.WebSocketSessionAware;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static io.micronaut.websocket.annotation.WebSocketComponent.DEFAULT_URI;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation declared on the client to indicate the class handles web socket frames.
 *
 * @author graemerocher
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@WebSocketComponent
@Introduction(interfaces = WebSocketSessionAware.class)
@Type(ClientWebSocketInterceptor.class)
@DefaultScope(Prototype.class)
public @interface ClientWebSocket {

    /**
     * @return The URI of the action
     */
    @AliasFor(member = "uri")
    @AliasFor(annotation = WebSocketComponent.class, member = "value")
    @AliasFor(annotation = WebSocketComponent.class, member = "uri")
    String value() default DEFAULT_URI;

    /**
     * @return The URI of the action
     */
    @AliasFor(member = "value")
    @AliasFor(annotation = WebSocketComponent.class, member = "value")
    @AliasFor(annotation = WebSocketComponent.class, member = "uri")
    String uri() default DEFAULT_URI;

    /**
     * @return The WebSocket version to use to connect
     */
    @AliasFor(annotation = WebSocketComponent.class, member = "version")
    WebSocketVersion version() default WebSocketVersion.V13;

    /**
     * @return The Sec-WebSocket-Protocol header field is used in the WebSocket opening handshake.
     */
    String subprotocol() default "";
}
