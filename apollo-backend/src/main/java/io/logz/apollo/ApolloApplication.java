package io.logz.apollo;

import com.google.inject.Injector;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.governator.lifecycle.LifecycleManager;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.di.ApolloModule;
import io.logz.apollo.di.ApolloMyBatisModule;
import io.logz.apollo.scm.GithubConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 11/20/16.
 */
public class ApolloApplication {

    private static final Logger logger = LoggerFactory.getLogger(ApolloApplication.class);

    private final ApolloConfiguration configuration;
    private LifecycleManager lifecycleManager;
    private Injector injector;

    public ApolloApplication(ApolloConfiguration configuration) {
        this.configuration = requireNonNull(configuration);
    }

    public void start() {
        try {
            logger.info("Starting apollo..");
            ApolloMyBatis.initialize(configuration);
            GithubConnector.initialize(configuration);

            injector = LifecycleInjector.builder().withModules(
                    new ApolloModule(configuration),
                    new ApolloMyBatisModule(configuration)
            ).build().createInjector();

            lifecycleManager = injector.getInstance(LifecycleManager.class);
            lifecycleManager.start();

            logger.info("Apollo started");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        logger.info("Cleaning up..");
        if (lifecycleManager != null) lifecycleManager.close();
    }

    public Injector getInjector() {
        return injector;
    }

    public static void main(String[] args) {
        ApolloConfiguration configuration = ApolloConfiguration.parseConfigurationFromResources();
        ApolloApplication application = new ApolloApplication(configuration);

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(application::shutdown));

            application.start();
            new CountDownLatch(1).await();
        } catch (Exception e) {
            if (e instanceof InterruptedException) return;

            logger.error(e.getMessage(), e);
            application.shutdown();
            System.exit(1);
        }
    }

}
