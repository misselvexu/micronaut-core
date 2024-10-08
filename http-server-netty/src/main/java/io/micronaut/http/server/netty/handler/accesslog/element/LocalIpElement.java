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
package io.micronaut.http.server.netty.handler.accesslog.element;

import io.micronaut.core.annotation.NonNull;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.Set;

/**
 * LocalIpElement LogElement. The local IP address.
 *
 * @author croudet
 * @since 2.0
 */
final class LocalIpElement implements LogElement {
    /**
     * The local ip marker.
     */
    public static final String LOCAL_IP = "A";

    /**
     * The LocalIpElement instance.
     */
    static final LocalIpElement INSTANCE = new LocalIpElement();

    private LocalIpElement() {

    }

    @Override
    public Set<Event> events() {
        return Event.REQUEST_HEADERS_EVENTS;
    }

    @Override
    public String onRequestHeaders(@NonNull ConnectionMetadata metadata, @NonNull String method, @NonNull HttpHeaders headers, @NonNull String uri, @NonNull String protocol) {
        return metadata.localAddress().flatMap(ConnectionMetadata::getHostAddress).orElse(ConstantElement.UNKNOWN_VALUE);
    }

    @Override
    public LogElement copy() {
        return this;
    }

    @Override
    public String toString() {
        return '%' + LOCAL_IP;
    }
}
