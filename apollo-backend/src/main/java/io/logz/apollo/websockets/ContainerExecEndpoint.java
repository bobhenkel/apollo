package io.logz.apollo.websockets;

import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.logz.apollo.common.QueryStringParser;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.kubernetes.KubernetesHandler;
import io.logz.apollo.kubernetes.KubernetesHandlerFactory;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by roiravhon on 5/23/17.
 */
@SuppressWarnings("ALL")
@ServerEndpoint(value = "/exec/pod/{podName}/container/{containerName}")
public class ContainerExecEndpoint {

    static final String QUERY_STRING_ENVIRONMENT_KEY = "environment";
    static final String QUERY_STRING_SERVICE_KEY = "service";

    private static final Logger logger = LoggerFactory.getLogger(ContainerExecEndpoint.class);
    private final ExecWebSocketSessionStore execWebSocketSessionStore;

    public ContainerExecEndpoint() {
        execWebSocketSessionStore = ExecWebSocketSessionStore.getInstance();
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("podName") String podName, @PathParam("containerName") String containerName) {

        int environmentId = QueryStringParser.getIntFromQueryString(session.getQueryString(), QUERY_STRING_ENVIRONMENT_KEY);
        int serviceId = QueryStringParser.getIntFromQueryString(session.getQueryString(), QUERY_STRING_SERVICE_KEY);

        try (ApolloMyBatis.ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {

            EnvironmentDao environmentDao = apolloMyBatisSession.getDao(EnvironmentDao.class);
            ServiceDao serviceDao = apolloMyBatisSession.getDao(ServiceDao.class);

            Environment environment = environmentDao.getEnvironment(environmentId);
            Service service = serviceDao.getService(serviceId);

            // Get default shell
            String defaultShell = Optional.ofNullable(service.getDefaultShell()).orElse("/bin/bash");

            KubernetesHandler kubernetesHandler = KubernetesHandlerFactory.getOrCreateKubernetesHandler(environment);

            logger.info("Opening ExecWatch to container {} in pod {} in environment {} related to service {}", containerName, podName, environment.getName(), service.getName());
            ExecWatch execWatch = kubernetesHandler.getExecWatch(podName, containerName,defaultShell);
            ExecutorService executor = Executors.newFixedThreadPool(2);

            SessionExecModel sessionExecModel = new SessionExecModel(execWatch, executor);
            openReaderThreads(session, sessionExecModel);

            // Initialize the ExecWatch against kubernetes handler
            execWebSocketSessionStore.addSession(session, sessionExecModel);
        }
    }

    @OnClose
    public void onClose(Session session) {
        logger.info("Closing session..");
        SessionExecModel sessionExecModel = execWebSocketSessionStore.getSessionExecModel(session);
        sessionExecModel.getExecutor().shutdownNow();
        sessionExecModel.getExecWatch().close();
        execWebSocketSessionStore.deleteSession(session);
    }

    @OnMessage
    public void onMessageRecieved(Session session, String command) {
        SessionExecModel sessionModel = execWebSocketSessionStore.getSessionExecModel(session);
        ExecWatch execWatch = sessionModel.getExecWatch();

        if (execWatch == null) {
            logger.info("Got message to an unknown ExecWatch, ignoring!");
            return;
        }
        try {
            execWatch.getInput().write(command.getBytes());

        } catch (IOException e) {
            logger.warn("Got IO Exception while writing to the ExecWatch!", e);
        }
    }

    private void openReaderThreads(Session session, SessionExecModel sessionExecModel) {
        sessionExecModel.getExecutor().execute(() -> readFromStreamToSession(sessionExecModel.getExecWatch().getOutput(), session));
        sessionExecModel.getExecutor().execute(() -> readFromStreamToSession(sessionExecModel.getExecWatch().getError(), session));
    }

    private void readFromStreamToSession(InputStream inputStream, Session session) {
        try {
            Reader reader = new InputStreamReader(inputStream);
            while (!Thread.interrupted()) {
                try {
                    char character = (char) reader.read();
                    session.getBasicRemote().sendObject(character);
                } catch (InterruptedIOException e) {
                    break;
                } catch (IOException e) {
                    if (!Thread.interrupted()) {
                        logger.warn("Got IOException while writing to websocket, bailing..", e);
                        break;
                    } else {
                        Thread.currentThread().interrupt();
                    }
                } catch (Exception e) {
                    logger.warn("Got exception while reading and sending line to websocket, continuing.. ", e);
                }
            }
        } catch (Exception e) {
            logger.error("Got unhandled exception while reading from input stream, swallowing", e);
        }
    }
}
