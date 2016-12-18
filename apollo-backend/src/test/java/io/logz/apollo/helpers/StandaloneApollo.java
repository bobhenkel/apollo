package io.logz.apollo.helpers;

import io.logz.apollo.ApolloServer;
import io.logz.apollo.clients.ApolloTestAdminClient;
import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.configuration.ApolloConfiguration;

import javax.script.ScriptException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by roiravhon on 11/23/16.
 */
public class StandaloneApollo {

    private static StandaloneApollo instance;
    private final ApolloServer server;

    private ApolloConfiguration apolloConfiguration;
    private ApolloMySQL apolloMySQL;

    private StandaloneApollo() throws ScriptException, SQLException, IOException {

        apolloConfiguration = ApolloConfiguration.parseConfigurationFromResources();

        // Start DB and match configuration
        apolloMySQL = new ApolloMySQL();
        apolloConfiguration.setDbHost(apolloMySQL.getContainerIpAddress());
        apolloConfiguration.setDbPort(apolloMySQL.getMappedPort());
        apolloConfiguration.setDbUser(apolloMySQL.getUsername());
        apolloConfiguration.setDbPassword(apolloMySQL.getPassword());
        apolloConfiguration.setDbSchema(apolloMySQL.getSchema());

        // Set free port for the api to listen to
        apolloConfiguration.setApiPort(Common.getAvailablePort());

        // Start REST Server
        server = new ApolloServer(apolloConfiguration);
        server.start();
    }

    public static StandaloneApollo getOrCreateServer() throws ScriptException, IOException, SQLException {

        if (instance == null) {
            instance = new StandaloneApollo();
        }
        return instance;
    }

    public ApolloTestClient createTestClient() {
        return new ApolloTestClient(apolloConfiguration);
    }

    public ApolloTestAdminClient createTestAdminClient() {
        return new ApolloTestAdminClient(apolloConfiguration);
    }
}
