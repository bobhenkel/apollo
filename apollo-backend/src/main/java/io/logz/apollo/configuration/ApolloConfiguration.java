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

    @VisibleForTesting
    public ApolloConfiguration(int dbPort, String dbHost, String dbUser, String dbPassword, String dbSchema) {
        this.dbPort = dbPort;
        this.dbHost = dbHost;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbSchema = dbSchema;
    }

    public static ApolloConfiguration parseConfigurationFromResources() {

        Config config = ConfigFactory.load();
        int dbPort = config.getInt("apollo.db.port");
        String dbHost = config.getString("apollo.db.host");
        String dbUser = config.getString("apollo.db.user");
        String dbPassword = config.getString("apollo.db.password");
        String dbSchema = config.getString("apollo.db.schema");

        return new ApolloConfiguration(dbPort, dbHost, dbUser, dbPassword, dbSchema);
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
}
