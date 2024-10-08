package io.micronaut.http.server.netty;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.server.netty.configuration.NettyHttpServerConfiguration;
import io.micronaut.runtime.ApplicationConfiguration;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Factory
@Requires(property = "spec.name", value = "NettyMultiServerSpec")
public class TestNettySecondaryServer {
    @Inject ApplicationConfiguration applicationConfiguration;
    @Inject NettyEmbeddedServerFactory embeddedServerFactory;

    @Named("secondary")
    @Bean(preDestroy = "stop")
    @Requires(property = "spec.name", value = "NettyMultiServerSpec")
    NettyEmbeddedServer secondaryServer() {
        NettyEmbeddedServer embeddedServer = embeddedServerFactory
                .build(new NettyHttpServerConfiguration(applicationConfiguration));
        embeddedServer.start();
        return embeddedServer;
    }
}
