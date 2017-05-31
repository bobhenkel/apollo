package io.logz.apollo.websockets;

import com.google.inject.Injector;

import javax.websocket.server.ServerEndpointConfig;

import static java.util.Objects.requireNonNull;

public class GuiceConfigurator extends ServerEndpointConfig.Configurator {

    private final Injector injector;

    public GuiceConfigurator(Injector injector) {
        this.injector = requireNonNull(injector);
    }

    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return injector.getInstance(endpointClass);
    }

}
