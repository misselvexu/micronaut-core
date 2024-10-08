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
package io.micronaut.http.client.exceptions;

import io.micronaut.core.annotation.Internal;

/**
 * @author Graeme Rocher
 * @since 1.0
 */
public class ContentLengthExceededException extends HttpClientException {

    /**
     * @param maxLength      The maximum length
     * @param receivedLength The received length
     */
    public ContentLengthExceededException(long maxLength, long receivedLength) {
        super("The received length [" + receivedLength + "] exceeds the maximum allowed content length [" + maxLength + "]");
    }

    /**
     * @param maxLength The maximum length
     */
    public ContentLengthExceededException(long maxLength) {
        super("The received length exceeds the maximum allowed content length [" + maxLength + "]");
    }

    /**
     * Constructor with a message, useful for adapting from
     * {@link io.micronaut.http.exceptions.ContentLengthExceededException}.
     *
     * @param message The message
     */
    @Internal
    public ContentLengthExceededException(String message) {
        super(message);
    }
}
