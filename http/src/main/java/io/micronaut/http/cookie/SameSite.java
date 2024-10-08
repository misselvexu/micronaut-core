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
package io.micronaut.http.cookie;

/**
 * Asserts that a cookie must not be sent with cross-origin requests, providing some protection against cross-site request forgery attacks (CSRF).
 *
 * Possible values for the SameSite attribute.
 * See <a href="https://tools.ietf.org/html/draft-ietf-httpbis-rfc6265bis-05">changes to RFC6265bis</a>
 *
 * @author croudet
 * @since 1.3.4
 */
public enum SameSite {
  /**
   * The cookie will be sent along with the GET request initiated by third party website.
   */
  Lax,
  /**
   * When the SameSite attribute is set as Strict, the cookie will not be sent along with requests initiated by third party websites.
   */
  Strict,
  /**
   * Allows third-party cookies to track users across sites. The None directive requires the Secure attribute.
   */
  None
}
