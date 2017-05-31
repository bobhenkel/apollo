package io.logz.apollo.di;

import com.google.inject.AbstractModule;
import io.logz.apollo.ApolloServer;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.kubernetes.KubernetesMonitor;
import io.logz.apollo.websockets.WebSocketServer;

import static java.util.Objects.requireNonNull;

public class ApolloModule extends AbstractModule {

    private final ApolloConfiguration configuration;

    public ApolloModule(ApolloConfiguration configuration) {
        this.configuration = requireNonNull(configuration);
    }

    @Override
    protected void configure() {
        bind(ApolloConfiguration.class).toInstance(configuration);
        bind(KubernetesMonitor.class).asEagerSingleton();
        bind(WebSocketServer.class).asEagerSingleton();
        bind(ApolloServer.class).asEagerSingleton();
    }

}
