package io.logz.apollo.helpers;

import io.logz.apollo.ApolloApplication;
import io.logz.apollo.clients.ApolloTestAdminClient;
import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.configuration.ApiConfiguration;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.configuration.DatabaseConfiguration;
import io.logz.apollo.configuration.KubernetesConfiguration;
import io.logz.apollo.configuration.ScmConfiguration;
import io.logz.apollo.configuration.WebsocketConfiguration;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.kubernetes.KubernetesMonitor;
import org.apache.commons.lang3.StringUtils;
import org.conf4j.core.ConfigurationProvider;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class StandaloneApollo {

    private static StandaloneApollo instance;
    private static String hostname = "localhost";
    private static String protocol = "http";

    private final ApolloApplication apolloApplication;
    private final KubernetesMonitor kubernetesMonitor;

    private ApolloConfiguration apolloConfiguration;

    private StandaloneApollo() throws ScriptException, SQLException, IOException {
        System.setProperty(KubernetesMonitor.LOCAL_RUN_PROPERTY, "true");

        // Start DB and match configuration
        ApolloMySQL apolloMySQL = new ApolloMySQL();
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(
                apolloMySQL.getMappedPort(),
                apolloMySQL.getContainerIpAddress(),
                apolloMySQL.getUsername(),
                apolloMySQL.getPassword(),
                apolloMySQL.getSchema()
        );

        apolloConfiguration = new ApolloConfiguration(
                new ApiConfiguration(Common.getAvailablePort(), "0.0.0.0", "secret"),
                databaseConfiguration,
                new KubernetesConfiguration(5),
                new ScmConfiguration(StringUtils.EMPTY, StringUtils.EMPTY),
                new WebsocketConfiguration(Common.getAvailablePort(), 5)
        );

        // Start apollo
        apolloApplication = new ApolloApplication(createConfigurationProvider(apolloConfiguration));
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
        return new ApolloTestClient(hostname, apolloConfiguration.getApi().getPort(), protocol);
    }

    public <T> T getInstance(Class<T> clazz) {
        return apolloApplication.getInjector().getInstance(clazz);
    }

    public ApolloTestAdminClient createTestAdminClient() {
        UserDao userDao = getInstance(UserDao.class);
        return new ApolloTestAdminClient(hostname, apolloConfiguration.getApi().getPort(), protocol, userDao);
    }

    private <T> ConfigurationProvider<T> createConfigurationProvider(T configuration) {
        return new ConfigurationProvider<T>() {
            @Override
            public T get() {
                return configuration;
            }

            @Override
            public <C> ConfigurationProvider<C> createConfigurationProvider(Function<T, C> extractor) {
                return null;
            }

            @Override
            public void registerChangeListener(BiConsumer<T, T> listener) {}
        };
    }

}
