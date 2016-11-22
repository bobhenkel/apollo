package io.logz.apollo;

import io.logz.apollo.auth.PasswordManager;
import io.logz.apollo.auth.User;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.database.ApolloMyBatis;
import org.rapidoid.setup.App;
import org.rapidoid.setup.My;
import org.rapidoid.setup.On;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by roiravhon on 11/20/16.
 */

public class ApolloMain {

    private static final Logger logger = LoggerFactory.getLogger(ApolloMain.class);

    public static void main(String[] args) {

        try {
            logger.info("Started apollo main");
            ApolloConfiguration apolloConfiguration = ApolloConfiguration.parseConfigurationFromResources();
            ApolloMyBatis.initialize(apolloConfiguration);

            // Initialize the REST API server
            On.address(apolloConfiguration.getApiListen()).port(apolloConfiguration.getApiPort());
            App.bootstrap(args).auth();

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

    public static void shutdown() {
        logger.info("Cleaning up..");
        ApolloMyBatis.close();
    }
}
