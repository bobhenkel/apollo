package io.logz.apollo.helpers;

import io.logz.apollo.ApolloApplication;
import io.logz.apollo.clients.ApolloTestAdminClient;
import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.kubernetes.KubernetesMonitor;

import javax.script.ScriptException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by roiravhon on 11/23/16.
 */
public class StandaloneApollo {

    private static StandaloneApollo instance;
    private static String hostname = "localhost";
    private static String protocol = "http";

    private final ApolloApplication apolloApplication;
    private final KubernetesMonitor kubernetesMonitor;

    private ApolloConfiguration apolloConfiguration;

    private StandaloneApollo() throws ScriptException, SQLException, IOException {
        System.setProperty(KubernetesMonitor.LOCAL_RUN_PROPERTY, "true");

        apolloConfiguration = ApolloConfiguration.parseConfigurationFromResources();

        // Start DB and match configuration
        ApolloMySQL apolloMySQL = new ApolloMySQL();
        apolloConfiguration.setDbHost(apolloMySQL.getContainerIpAddress());
        apolloConfiguration.setDbPort(apolloMySQL.getMappedPort());
        apolloConfiguration.setDbUser(apolloMySQL.getUsername());
        apolloConfiguration.setDbPassword(apolloMySQL.getPassword());
        apolloConfiguration.setDbSchema(apolloMySQL.getSchema());

        // Set free port for the api to listen to
        apolloConfiguration.setApiPort(Common.getAvailablePort());

        // Start apollo
        apolloApplication = new ApolloApplication(apolloConfiguration);
        apolloApplication.start();

        // Get Kubernetes monitor, the monitor is stopped by default in tests because usually will want to inject mock first
        kubernetesMonitor = apolloApplication.getInjector().getInstance(KubernetesMonitor.class);
        Runtime.getRuntime().addShutdownHook(new Thread(apolloApplication::shutdown));
    }

    public static StandaloneApollo getOrCreateServer() throws ScriptException, IOException, SQLException {
        if (instance == null) {
            instance = new StandaloneApollo();
        }

        return instance;
    }

    public void startKubernetesMonitor() {
        System.setProperty(KubernetesMonitor.LOCAL_RUN_PROPERTY, "false");
        kubernetesMonitor.start();
    }

    public ApolloTestClient createTestClient() {
        return new ApolloTestClient(hostname, apolloConfiguration.getApiPort(), protocol);
    }

    public <T> T getInstance(Class<T> clazz) {
        return apolloApplication.getInjector().getInstance(clazz);
    }

    public ApolloTestAdminClient createTestAdminClient() {
        UserDao userDao = getInstance(UserDao.class);
        return new ApolloTestAdminClient(hostname, apolloConfiguration.getApiPort(), protocol, userDao);
    }

}
