package io.logz.apollo.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiConfiguration {

    private int port;
    private String listen;
    private String secret;

    @JsonCreator
    public ApiConfiguration(@JsonProperty("port") int port,
                            @JsonProperty("listen") String listen,
                            @JsonProperty("secret") String secret) {
        this.port = port;
        this.listen = listen;
        this.secret = secret;
    }

    public int getPort() {
        return port;
    }

    public String getListen() {
        return listen;
    }

    public String getSecret() {
        return secret;
    }

}
