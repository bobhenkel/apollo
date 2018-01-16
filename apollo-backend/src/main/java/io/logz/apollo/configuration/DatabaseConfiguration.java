package io.logz.apollo.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DatabaseConfiguration {

    private int port;
    private String host;
    private String user;
    private String password;
    private String schema;

    @JsonCreator
    public DatabaseConfiguration(@JsonProperty("port") int port,
                                 @JsonProperty("host") String host,
                                 @JsonProperty("user") String user,
                                 @JsonProperty("password") String password,
                                 @JsonProperty("schema") String schema) {
        this.port = port;
        this.host = host;
        this.user = user;
        this.password = password;
        this.schema = schema;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getSchema() {
        return schema;
    }

}
