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
package io.micronaut.http.client;

import io.micronaut.context.annotation.BootstrapContextCompatible;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Primary;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.ssl.ClientSslConfiguration;
import io.micronaut.runtime.ApplicationConfiguration;
import jakarta.inject.Inject;

/**
 * The default configuration if no explicit configuration is specified for an HTTP client.
 *
 * @author graemerocher
 * @since 1.0
 */
@ConfigurationProperties(DefaultHttpClientConfiguration.PREFIX)
@BootstrapContextCompatible
@Primary
public class DefaultHttpClientConfiguration extends HttpClientConfiguration {

    /**
     * Prefix for HTTP Client settings.
     */
    public static final String PREFIX = "micronaut.http.client";
    private final DefaultConnectionPoolConfiguration connectionPoolConfiguration;
    private final DefaultWebSocketCompressionConfiguration webSocketCompressionConfiguration;
    private final DefaultHttp2ClientConfiguration http2Configuration;

    /**
     * Default constructor.
     */
    public DefaultHttpClientConfiguration() {
        this.connectionPoolConfiguration = new DefaultConnectionPoolConfiguration();
        this.webSocketCompressionConfiguration = new DefaultWebSocketCompressionConfiguration();
        this.http2Configuration = new DefaultHttp2ClientConfiguration();
    }

    /**
     * @param connectionPoolConfiguration The connection pool configuration
     * @param applicationConfiguration The application configuration
     * @deprecated Use {@link DefaultHttpClientConfiguration(DefaultConnectionPoolConfiguration, DefaultWebSocketCompressionConfiguration, DefaultHttp2ClientConfiguration , ApplicationConfiguration)} instead.
     */
    @Deprecated(since = "4.3.0")
    public DefaultHttpClientConfiguration(DefaultConnectionPoolConfiguration connectionPoolConfiguration, ApplicationConfiguration applicationConfiguration) {
        this(connectionPoolConfiguration, new DefaultWebSocketCompressionConfiguration(), applicationConfiguration);
    }

    /**
     * @param connectionPoolConfiguration The connection pool configuration
     * @param webSocketCompressionConfiguration The WebSocket compression configuration
     * @param applicationConfiguration The application configuration
     * @deprecated Use {@link DefaultHttpClientConfiguration(DefaultConnectionPoolConfiguration, DefaultWebSocketCompressionConfiguration, DefaultHttp2ClientConfiguration , ApplicationConfiguration)} instead.
     */
    @Deprecated(since = "4.6.0")
    public DefaultHttpClientConfiguration(DefaultConnectionPoolConfiguration connectionPoolConfiguration,
                                          DefaultWebSocketCompressionConfiguration webSocketCompressionConfiguration,
                                          ApplicationConfiguration applicationConfiguration) {
        this(connectionPoolConfiguration, webSocketCompressionConfiguration, new DefaultHttp2ClientConfiguration(), applicationConfiguration);
    }

    /**
     * @param connectionPoolConfiguration The connection pool configuration
     * @param webSocketCompressionConfiguration The WebSocket compression configuration
     * @param http2Configuration The HTTP/2 configuration
     * @param applicationConfiguration The application configuration
     */
    @Inject
    public DefaultHttpClientConfiguration(DefaultConnectionPoolConfiguration connectionPoolConfiguration,
                                          DefaultWebSocketCompressionConfiguration webSocketCompressionConfiguration,
                                          DefaultHttp2ClientConfiguration http2Configuration,
                                          ApplicationConfiguration applicationConfiguration) {
        super(applicationConfiguration);
        this.connectionPoolConfiguration = connectionPoolConfiguration;
        this.webSocketCompressionConfiguration = webSocketCompressionConfiguration;
        this.http2Configuration = http2Configuration;
    }

    @Override
    public ConnectionPoolConfiguration getConnectionPoolConfiguration() {
        return connectionPoolConfiguration;
    }

    @Override
    public WebSocketCompressionConfiguration getWebSocketCompressionConfiguration() {
        return webSocketCompressionConfiguration;
    }

    /**
     * Uses the default SSL configuration.
     *
     * @param sslConfiguration The SSL configuration
     */
    @Inject
    public void setClientSslConfiguration(@Nullable ClientSslConfiguration sslConfiguration) {
        if (sslConfiguration != null) {
            super.setSslConfiguration(sslConfiguration);
        }
    }

    @Override
    public Http2ClientConfiguration getHttp2Configuration() {
        return http2Configuration;
    }

    /**
     * The default connection pool configuration.
     */
    @ConfigurationProperties(ConnectionPoolConfiguration.PREFIX)
    @BootstrapContextCompatible
    @Primary
    public static class DefaultConnectionPoolConfiguration extends ConnectionPoolConfiguration {
    }

    /**
     * The default WebSocket compression configuration.
     */
    @ConfigurationProperties(WebSocketCompressionConfiguration.PREFIX)
    @BootstrapContextCompatible
    @Primary
    public static class DefaultWebSocketCompressionConfiguration extends WebSocketCompressionConfiguration {
    }

    /**
     * The default HTTP/2 configuration.
     */
    @ConfigurationProperties(Http2ClientConfiguration.PREFIX)
    @BootstrapContextCompatible
    @Primary
    public static class DefaultHttp2ClientConfiguration extends Http2ClientConfiguration {
    }
}
