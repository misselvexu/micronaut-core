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
package io.micronaut.core.type;


import io.micronaut.core.annotation.NonNull;

/**
 * Common interface for all mutable header types.
 *
 * @author graemerocher
 * @since 1.0
 */
public interface MutableHeaders extends Headers {

    /**
     * Add a header for the given name and value.
     *
     * @param header The head name
     * @param value  The value
     * @return This headers object
     */
    MutableHeaders add(CharSequence header, CharSequence value);

    /**
     * Removes a header.
     *
     * @param header The header to remove
     * @return These headers
     */
    MutableHeaders remove(CharSequence header);

    /**
     * Sets an HTTP header overriding any existing value.
     * @param header The header
     * @param value The value
     * @return This headers
     * @since 1.3.3
     */
    @NonNull
    default MutableHeaders set(@NonNull CharSequence header, @NonNull CharSequence value) {
        remove(header);
        add(header, value);
        return this;
    }

    /**
     * Sets an HTTP header if missing.
     *
     * @param header The header
     * @param value  The value
     * @return This headers
     * @since 4.6
     */
    @NonNull
    default MutableHeaders setIfMissing(@NonNull CharSequence header, @NonNull CharSequence value) {
        if (!contains(header.toString())) {
            add(header, value);
        }
        return this;
    }
}
