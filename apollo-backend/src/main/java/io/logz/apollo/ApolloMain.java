package io.logz.apollo;

import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.database.ApolloMyBatis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by roiravhon on 11/20/16.
 */

public class ApolloMain {

    private static final Logger logger = LoggerFactory.getLogger(ApolloMain.class);
    private static boolean hasError = false;

    public static void main(String[] args) {

        try {
            logger.info("Started apollo main");
            ApolloConfiguration apolloConfiguration = ApolloConfiguration.parseConfigurationFromResources();
            ApolloMyBatis.initialize(apolloConfiguration);


        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            hasError = true;

        } finally {
            logger.info("Cleaning up..");
            ApolloMyBatis.close();

            if (hasError) {
                System.exit(1);
            }
        }
    }
}
