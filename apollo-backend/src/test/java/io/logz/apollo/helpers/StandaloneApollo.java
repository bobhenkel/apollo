package io.logz.apollo.helpers;

import com.google.inject.Guice;
import io.logz.apollo.ApolloServer;
import io.logz.apollo.clients.ApolloTestAdminClient;
import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.di.ApolloModule;
import io.logz.apollo.kubernetes.KubernetesMonitor;

import javax.script.ScriptException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by roiravhon on 11/23/16.
 */
public class StandaloneApollo {

    private static StandaloneApollo instance;
    private final ApolloServer server;
    private final KubernetesMonitor kubernetesMonitor;

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
        server = new ApolloServer(apolloConfiguration, Guice.createInjector(new ApolloModule()));
        server.start();

        // Create Kubernetes monitor, but dont start it yet (usually will want to inject mock first)
        kubernetesMonitor = new KubernetesMonitor(apolloConfiguration);
    }

    public static StandaloneApollo getOrCreateServer() throws ScriptException, IOException, SQLException {

        if (instance == null) {
            instance = new StandaloneApollo();
        }
        return instance;
    }

    public void startKubernetesMonitor() {
        kubernetesMonitor.start();
    }

    public ApolloTestClient createTestClient() {
        return new ApolloTestClient(apolloConfiguration);
    }

    public ApolloTestAdminClient createTestAdminClient() {
        return new ApolloTestAdminClient(apolloConfiguration);
    }
}
