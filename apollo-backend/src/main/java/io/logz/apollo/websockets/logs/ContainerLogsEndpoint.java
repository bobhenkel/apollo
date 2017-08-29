package io.logz.apollo.websockets.logs;

import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import io.logz.apollo.common.QueryStringParser;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.kubernetes.KubernetesHandler;
import io.logz.apollo.kubernetes.KubernetesHandlerStore;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;
import io.logz.apollo.websockets.WebsocketWriter;
import io.logz.apollo.websockets.exec.SessionExecModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.requireNonNull;

@ServerEndpoint(value = "/logs/pod/{podName}/container/{containerName}")
public class ContainerLogsEndpoint {

    private static final String QUERY_STRING_ENVIRONMENT_KEY = "environment";
    private static final String QUERY_STRING_SERVICE_KEY = "service";

    private static final Logger logger = LoggerFactory.getLogger(ContainerLogsEndpoint.class);
    private final LogsWebSocketSessionStore logsWebSocketSessionStore;
    private final KubernetesHandlerStore kubernetesHandlerStore;
    private final EnvironmentDao environmentDao;
    private final ServiceDao serviceDao;

    @Inject
    public ContainerLogsEndpoint(LogsWebSocketSessionStore logsWebSocketSessionStore,
                                 KubernetesHandlerStore kubernetesHandlerStore,
                                 EnvironmentDao environmentDao, ServiceDao serviceDao) {
        this.logsWebSocketSessionStore = requireNonNull(logsWebSocketSessionStore);
        this.kubernetesHandlerStore = requireNonNull(kubernetesHandlerStore);
        this.environmentDao = requireNonNull(environmentDao);
        this.serviceDao = requireNonNull(serviceDao);
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("podName") String podName, @PathParam("containerName") String containerName) {
        int environmentId = QueryStringParser.getIntFromQueryString(session.getQueryString(), QUERY_STRING_ENVIRONMENT_KEY);
        int serviceId = QueryStringParser.getIntFromQueryString(session.getQueryString(), QUERY_STRING_SERVICE_KEY);

        Environment environment = environmentDao.getEnvironment(environmentId);
        Service service = serviceDao.getService(serviceId);

        KubernetesHandler kubernetesHandler = kubernetesHandlerStore.getOrCreateKubernetesHandler(environment);

        logger.info("Opening LogWatch to container {} in pod {} in environment {} related to service {}",
                containerName, podName, environment.getName(), service.getName());


        LogWatch logWatch = kubernetesHandler.getLogWatch(podName, containerName);
        ExecutorService executor = Executors.newFixedThreadPool(1);

        SessionLogWatchModel sessionLogWatchModel = new SessionLogWatchModel(logWatch, executor);
        openReaderThread(session, sessionLogWatchModel);

        // Initialize the ExecWatch against kubernetes handler
        logsWebSocketSessionStore.addSession(session, sessionLogWatchModel);
    }

    @OnClose
    public void onClose(Session session) {
        logger.info("Closing session..");
        SessionLogWatchModel sessionLogWatchModel = logsWebSocketSessionStore.getSessionLogWatchModel(session);
        sessionLogWatchModel.getExecutor().shutdownNow();
        sessionLogWatchModel.getLogWatch().close();
        logsWebSocketSessionStore.deleteSession(session);
    }

    @OnMessage
    public void onMessageReceived(Session session, String command) {
    }

    private void openReaderThread(Session session, SessionLogWatchModel sessionLogWatchModel) {
        sessionLogWatchModel.getExecutor().execute(() -> WebsocketWriter.readFromStreamToSession(sessionLogWatchModel.getLogWatch().getOutput(), session));
    }
}
