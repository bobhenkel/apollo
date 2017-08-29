package io.logz.apollo.websockets.exec;

import javax.inject.Singleton;
import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by roiravhon on 5/23/17.
 */
@Singleton
public class ExecWebSocketSessionStore {

    private final Map<Session, SessionExecModel> sessionExecWatchMap;

    public ExecWebSocketSessionStore() {
        this.sessionExecWatchMap = new ConcurrentHashMap<>();
    }

    void addSession(Session session, SessionExecModel sessionExecModel) {
        sessionExecWatchMap.put(session, sessionExecModel);
    }

    void deleteSession(Session session) {
        sessionExecWatchMap.remove(session);
    }

    SessionExecModel getSessionExecModel(Session session) {
        return sessionExecWatchMap.get(session);
    }

}
