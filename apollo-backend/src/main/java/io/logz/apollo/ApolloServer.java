package io.logz.apollo;

import com.google.inject.Injector;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.scm.GithubConnector;
import org.rapidoid.integrate.GuiceBeans;
import org.rapidoid.integrate.Integrate;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;

import javax.inject.Inject;
import javax.inject.Singleton;

import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 11/22/16.
 */
@Singleton
public class ApolloServer {

    private final ApolloConfiguration configuration;
    private final Injector injector;

    @Inject
    public ApolloServer(ApolloConfiguration configuration, Injector injector) {
        this.configuration = requireNonNull(configuration);
        this.injector = requireNonNull(injector);
    }

    public void start() {
        ApolloMyBatis.initialize(configuration);
        GithubConnector.initialize(configuration);

        String[] args = new String[] {
                "secret=" + configuration.getSecret(),
                "on.address=" + configuration.getApiListen(),
                "on.port=" + configuration.getApiPort()
        };

        // Initialize the REST API server
        On.changes().ignore();
        On.address(configuration.getApiListen()).port(configuration.getApiPort());

        GuiceBeans beans = Integrate.guice(injector);
        App.register(beans);
        App.bootstrap(args).auth();
    }

    public void stop() {
        // Future cleanups..
    }
}
