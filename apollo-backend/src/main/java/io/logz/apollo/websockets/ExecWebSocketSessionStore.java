package io.logz.apollo.websockets;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by roiravhon on 5/23/17.
 */
public class ExecWebSocketSessionStore {

    private final Map<Session, SessionExecModel> sessionExecWatchMap;
    private static ExecWebSocketSessionStore instance;

    private ExecWebSocketSessionStore() {
        this.sessionExecWatchMap = new ConcurrentHashMap<>();
    }

    public static ExecWebSocketSessionStore getInstance() {
        if (instance == null) {
            instance = new ExecWebSocketSessionStore();
        }

        return instance;
    }

    public void addSession(Session session, SessionExecModel sessionExecModel) {
        sessionExecWatchMap.put(session, sessionExecModel);
    }

    public void deleteSession(Session session) {
        sessionExecWatchMap.remove(session);
    }

    public SessionExecModel getSessionExecModel(Session session) {
        return sessionExecWatchMap.get(session);
    }
}
