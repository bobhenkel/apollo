package io.logz.apollo.websockets.exec;

import io.fabric8.kubernetes.client.dsl.ExecWatch;

import java.util.concurrent.ExecutorService;

/**
 * Created by roiravhon on 5/28/17.
 */
public class SessionExecModel {

    private final ExecWatch execWatch;
    private final ExecutorService executor;

    public SessionExecModel(ExecWatch execWatch, ExecutorService executor) {
        this.execWatch = execWatch;
        this.executor = executor;
    }

    public ExecWatch getExecWatch() {
        return execWatch;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

}
