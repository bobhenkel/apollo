package io.logz.apollo;

import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by roiravhon on 1/17/17.
 */
public class LockService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LockService.class);
    private static LockService instance;
    private HashMap<String, Boolean> lockMap;

    private LockService() {
        lockMap = new HashMap<>();
    }

    private static LockService getInstance() {
        if (instance == null) {
            instance = new LockService();
        }

        return instance;
    }

    public static synchronized boolean getAndObtainLock(String lockName) {
        LockService lockService = getInstance();
        Boolean lockStatus = lockService.lockMap.get(lockName);
        if (lockStatus == null || !lockStatus) {
            logger.debug("Got request to lock for key {}, granted.", lockName);
            lockService.lockMap.put(lockName, true);
            return true;

        } else {
            logger.debug("Got request to lock for key {}, denied.", lockName);
            return false;
        }
    }

    public static synchronized void releaseLock(String lockName) {
        LockService lockService = getInstance();
        Boolean lockStatus = lockService.lockMap.get(lockName);
        if (lockStatus == null) {
            logger.debug("Got request to release a lock for key {}, but its not set. Ignoring..", lockName);
        } else {
            logger.debug("Got request to release a lock for key {}. released.", lockName);
            lockService.lockMap.put(lockName, false);
        }
    }
}
