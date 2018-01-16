package io.logz.apollo.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WebsocketConfiguration {

    private int port;
    private int idleTimeoutSeconds;

    @JsonCreator
    public WebsocketConfiguration(@JsonProperty("port") int port,
                                  @JsonProperty("idleTimeoutSeconds") int idleTimeoutSeconds) {
        this.port = port;
        this.idleTimeoutSeconds = idleTimeoutSeconds;
    }

    public int getPort() {
        return port;
    }

    public int getIdleTimeoutSeconds() {
        return idleTimeoutSeconds;
    }

}
