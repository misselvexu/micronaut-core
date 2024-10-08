/*
 * Copyright 2017-2024 original authors
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
package io.micronaut.http.body;

import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.NonNull;

/**
 * Combination of {@link CloseableByteBody} and {@link AvailableByteBody}. See their
 * documentation for details.
 *
 * @author Jonas Konrad
 * @since 4.5.0
 */
@Experimental
public interface CloseableAvailableByteBody extends AvailableByteBody, CloseableByteBody {
    /**
     * {@inheritDoc}
     *
     * @deprecated This method is unnecessary for {@link AvailableByteBody}, it does nothing.
     */
    @SuppressWarnings("deprecation")
    @Override
    @NonNull
    @Deprecated
    default CloseableAvailableByteBody allowDiscard() {
        return this;
    }
}
