package io.logz.apollo;

import io.logz.apollo.configuration.ApolloConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by roiravhon on 11/20/16.
 */

public class ApolloMain {

    private static final Logger logger = LoggerFactory.getLogger(ApolloMain.class);
    private static ApolloServer apolloServer;

    public static void main(String[] args) {

        try {
            logger.info("Started apollo main");
            ApolloConfiguration apolloConfiguration = ApolloConfiguration.parseConfigurationFromResources();

            apolloServer = new ApolloServer(apolloConfiguration);
            apolloServer.start();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    ApolloMain.shutdown();
                }
            });

        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            shutdown();
            System.exit(1);
        }
    }

    private static void shutdown() {
        logger.info("Cleaning up..");
        apolloServer.stop();
    }
}
