package io.logz.apollo;

import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.scm.GithubConnector;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;

/**
 * Created by roiravhon on 11/22/16.
 */
public class ApolloServer {

    private final ApolloConfiguration configuration;

    public ApolloServer(ApolloConfiguration configuration) {
        this.configuration = configuration;
    }

    public void start() {
        ApolloMyBatis.initialize(configuration);
        GithubConnector.initialize(configuration);

        // Initialize the REST API server
        On.address(configuration.getApiListen()).port(configuration.getApiPort());
        App.bootstrap("").auth();
    }

    public void stop() {
        // Future cleanups..
    }
}
