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
import io.micronaut.core.convert.exceptions.ConversionErrorException;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.response.Error;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * Handles exception of type {@link ConversionErrorException}.
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@Singleton
@Produces
public class ConversionErrorHandler  extends ErrorExceptionHandler<ConversionErrorException> {

    /**
     * Constructor.
     * @param responseProcessor Error Response Processor
     */
    public ConversionErrorHandler(ErrorResponseProcessor<?> responseProcessor) {
        super(responseProcessor);
    }

    @Override
    @NonNull
    protected Error error(ConversionErrorException exception) {
        return new Error() {
                    @Override
                    public Optional<String> getPath() {
                        return Optional.of('/' + exception.getArgument().getName());
                    }

                    @Override
                    public String getMessage() {
                        return exception.getMessage();
                    }
                };
    }
}
