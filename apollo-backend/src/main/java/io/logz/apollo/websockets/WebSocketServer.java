package io.logz.apollo.websockets;

import io.logz.apollo.configuration.ApolloConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;

/**
 * Created by roiravhon on 5/23/17.
 */
@Singleton
public class WebSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    private final Server server;

    @Inject
    public WebSocketServer(ApolloConfiguration apolloConfiguration) {
        try {
            server = new Server(apolloConfiguration.getWsPort());
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
            context.addFilter(new FilterHolder(WebSocketAuthenticationFilter.class), "/*", null);
            server.setHandler(context);

            ServerContainer wsContainer = WebSocketServerContainerInitializer.configureContext(context);
            wsContainer.setDefaultMaxSessionIdleTimeout(apolloConfiguration.getWsIdleTimeoutSeconds() * 1000);
            wsContainer.addEndpoint(ContainerExecEndpoint.class);

        } catch (ServletException e) {
            throw new RuntimeException("Could not initialize WebSocket container!", e);
        } catch (DeploymentException e) {
            throw new RuntimeException("Could not register websocket endpoint!", e);
        }
    }

    @PostConstruct
    public void start() {
        try {
            logger.info("Starting Jetty server");
            server.start();
        } catch (Exception e) {
            throw new RuntimeException("Could not start Jetty server!", e);
        }
    }

    @PreDestroy
    public void stop() {
        if (server.isStarted() || server.isStarted()) {
            try {
                server.stop();

            } catch (Exception e) {
                logger.warn("Could not stop Jetty server!", e);
            }
        } else {
            logger.warn("Jetty already stopped, or did not start. can't stop!");
        }
    }
}
