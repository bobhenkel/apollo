package io.logz.apollo;

import com.netflix.governator.InjectorBuilder;
import com.netflix.governator.LifecycleInjector;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.di.ApolloModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by roiravhon on 11/20/16.
 */
public class ApolloMain {

    private static final Logger logger = LoggerFactory.getLogger(ApolloMain.class);
    private static LifecycleInjector lifecycleInjector;

    public static void main(String[] args) {
        try {
            logger.info("Started apollo main");
            Runtime.getRuntime().addShutdownHook(new Thread(ApolloMain::shutdown));
            ApolloConfiguration configuration = ApolloConfiguration.parseConfigurationFromResources();
            lifecycleInjector = InjectorBuilder.fromModules(
                            new ApolloModule(configuration)
                    ).createInjector();
            lifecycleInjector.awaitTermination();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            shutdown();
            System.exit(1);
        }
    }

    private static void shutdown() {
        logger.info("Cleaning up..");
        if (lifecycleInjector != null) lifecycleInjector.close();
    }

}
