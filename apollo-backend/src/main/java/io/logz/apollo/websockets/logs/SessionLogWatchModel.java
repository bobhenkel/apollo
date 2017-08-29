package io.logz.apollo.websockets.logs;

import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.dsl.LogWatch;

import java.util.concurrent.ExecutorService;

public class SessionLogWatchModel {

    private final LogWatch logWatch;
    private final ExecutorService executor;

    public SessionLogWatchModel(LogWatch logWatch, ExecutorService executor) {
        this.logWatch = logWatch;
        this.executor = executor;
    }

    public LogWatch getLogWatch() {
        return logWatch;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

}
