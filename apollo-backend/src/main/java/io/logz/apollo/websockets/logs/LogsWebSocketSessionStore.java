package io.logz.apollo.websockets.logs;

import javax.inject.Singleton;
import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class LogsWebSocketSessionStore {

    private final Map<Session, SessionLogWatchModel> sessionLogWatchModelMap;

    public LogsWebSocketSessionStore() {
        this.sessionLogWatchModelMap = new ConcurrentHashMap<>();
    }

    void addSession(Session session, SessionLogWatchModel sessionLogWatchModel) {
        sessionLogWatchModelMap.put(session, sessionLogWatchModel);
    }

    void deleteSession(Session session) {
        sessionLogWatchModelMap.remove(session);
    }

    SessionLogWatchModel getSessionLogWatchModel(Session session) {
        return sessionLogWatchModelMap.get(session);
    }

}
