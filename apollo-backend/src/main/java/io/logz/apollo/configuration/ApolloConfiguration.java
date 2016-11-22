package io.logz.apollo.configuration;

import com.google.common.annotations.VisibleForTesting;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by roiravhon on 11/20/16.
 */
public class ApolloConfiguration {

    private final int dbPort;
    private final String dbHost;
    private final String dbUser;
    private final String dbPassword;
    private final String dbSchema;
    private final String apiListen;
    private final int apiPort;

    @VisibleForTesting
    public ApolloConfiguration(int dbPort, String dbHost, String dbUser, String dbPassword, String dbSchema, String apiListen, int apiPort) {
        this.dbPort = dbPort;
        this.dbHost = dbHost;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbSchema = dbSchema;
        this.apiListen = apiListen;
        this.apiPort = apiPort;
    }

    public static ApolloConfiguration parseConfigurationFromResources() {

        Config config = ConfigFactory.load();

        // Database related
        int dbPort = config.getInt("apollo.db.port");
        String dbHost = config.getString("apollo.db.host");
        String dbUser = config.getString("apollo.db.user");
        String dbPassword = config.getString("apollo.db.password");
        String dbSchema = config.getString("apollo.db.schema");

        // API Related
        String apiListen = config.getString("apollo.api.listen");
        int apiPort = config.getInt("apollo.api.port");

        return new ApolloConfiguration(dbPort, dbHost, dbUser, dbPassword, dbSchema, apiListen, apiPort);
    }

    public int getDbPort() {
        return dbPort;
    }

    public String getDbHost() {
        return dbHost;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbSchema() {
        return dbSchema;
    }

    public String getApiListen() {
        return apiListen;
    }

    public int getApiPort() {
        return apiPort;
    }
}
