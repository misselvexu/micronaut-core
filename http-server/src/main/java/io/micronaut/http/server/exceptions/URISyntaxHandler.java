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
package io.micronaut.http.server.exceptions;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.response.Error;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import jakarta.inject.Singleton;

import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Handles exception of type {@link URISyntaxException}.
 *
 * @author Graeme Rocher
 * @since 2.0
 */
@Singleton
@Produces
public class URISyntaxHandler extends ErrorExceptionHandler<URISyntaxException> {

    /**
     * Constructor.
     * @param responseProcessor Error Response Processor
     */
    public URISyntaxHandler(ErrorResponseProcessor<?> responseProcessor) {
        super(responseProcessor);
    }

    @Override
    @NonNull
    protected Error error(URISyntaxException exception) {
        return new Error() {
            @Override
            public String getMessage() {
                return "Malformed URI";
            }

            @Override
            public Optional<String> getTitle() {
                return Optional.of("Malformed URI");
            }
        };
    }
}
