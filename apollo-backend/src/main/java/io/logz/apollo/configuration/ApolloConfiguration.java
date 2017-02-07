package io.logz.apollo.configuration;

import com.google.common.annotations.VisibleForTesting;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by roiravhon on 11/20/16.
 */
public class ApolloConfiguration {

    private int dbPort;
    private String dbHost;
    private String dbUser;
    private String dbPassword;
    private String dbSchema;
    private String apiListen;
    private int apiPort;
    private int monitorThreadFrequencySeconds;

    @VisibleForTesting
    public ApolloConfiguration(int dbPort, String dbHost, String dbUser, String dbPassword,
                               String dbSchema, String apiListen, int apiPort, int monitorThreadFrequencySeconds) {
        this.dbPort = dbPort;
        this.dbHost = dbHost;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbSchema = dbSchema;
        this.apiListen = apiListen;
        this.apiPort = apiPort;
        this.monitorThreadFrequencySeconds = monitorThreadFrequencySeconds;
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

        // Kubernetes related
        int monitorThreadFrequencySeconds = config.getInt("apollo.kubernetes.monitoringFrequencySeconds");

        return new ApolloConfiguration(dbPort, dbHost, dbUser, dbPassword, dbSchema, apiListen, apiPort, monitorThreadFrequencySeconds);
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

    public int getMonitorThreadFrequencySeconds() {
        return monitorThreadFrequencySeconds;
    }

    @VisibleForTesting
    public void setDbPort(int dbPort) {
        this.dbPort = dbPort;
    }

    @VisibleForTesting
    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    @VisibleForTesting
    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    @VisibleForTesting
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    @VisibleForTesting
    public void setDbSchema(String dbSchema) {
        this.dbSchema = dbSchema;
    }

    @VisibleForTesting
    public void setApiListen(String apiListen) {
        this.apiListen = apiListen;
    }

    @VisibleForTesting
    public void setApiPort(int apiPort) {
        this.apiPort = apiPort;
    }

    @VisibleForTesting
    public void setMonitorThreadFrequencySeconds(int monitorThreadFrequencySeconds) {
        this.monitorThreadFrequencySeconds = monitorThreadFrequencySeconds;
    }
}
