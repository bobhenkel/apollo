package io.logz.apollo.blockers;

/**
 * Created by roiravhon on 6/4/17.
 */
public class Blocker {

    private final String name;
    private final Integer serviceId;
    private final Integer environmentId;
    private final Boolean isActive;
    private final BlockerFunction blockerFunction;

    public Blocker(String name, Integer serviceId, Integer environmentId, Boolean isActive, BlockerFunction blockerFunction) {
        this.name = name;
        this.serviceId = serviceId;
        this.environmentId = environmentId;
        this.isActive = isActive;
        this.blockerFunction = blockerFunction;
    }

    public String getName() {
        return name;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public Integer getEnvironmentId() {
        return environmentId;
    }

    public Boolean getActive() {
        return isActive;
    }

    public BlockerFunction getBlockerFunction() {
        return blockerFunction;
    }
}
