package io.logz.apollo.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeploymentGroupsResponseObject {

    private final static String GROUP = "group";
    private final static String REASON = "reason";
    private final static String DEPLOYMENT = "deployment";

    private List<Map<String, Object>> successful;
    private List<Map<String, Object>> unsuccessful;

    public DeploymentGroupsResponseObject() {
        successful = new ArrayList<>();
        unsuccessful = new ArrayList<>();
    }

    public void addUnsuccessfulGroup(int groupId, String reason) {
        Map<String, Object> unsuccessfulGroup = new HashMap<>();
        unsuccessfulGroup.put(GROUP, groupId);
        unsuccessfulGroup.put(REASON, reason);
        unsuccessful.add(unsuccessfulGroup);
    }

    public void addSuccessfulGroup(int groupId, Deployment deployment) {
        Map<String, Object> successfulGroup = new HashMap<>();
        successfulGroup.put(GROUP, groupId);
        successfulGroup.put(DEPLOYMENT, deployment);
        successful.add(successfulGroup);
    }

    public List<Map<String, Object>> getUnsuccessful() {
        return unsuccessful;
    }

    public List<Map<String, Object>> getSuccessful() {
        return successful;
    }
}
