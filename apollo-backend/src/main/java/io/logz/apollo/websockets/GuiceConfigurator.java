package io.logz.apollo.websockets;

import com.google.inject.Injector;
import com.google.inject.Provider;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.server.ServerEndpointConfig;

import static java.util.Objects.requireNonNull;

@Singleton
public class GuiceConfigurator extends ServerEndpointConfig.Configurator {

    private final Provider<Injector> injectorProvider;

    @Inject
    public GuiceConfigurator(Provider<Injector> injectorProvider) {
        this.injectorProvider = requireNonNull(injectorProvider);
    }

    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return injectorProvider.get().getInstance(endpointClass);
    }

}
