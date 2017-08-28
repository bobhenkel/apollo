package io.logz.apollo.models;

import java.util.List;

/**
 * Created by roiravhon on 2/27/17.
 */
public class PodStatus {

    private String name;
    private String hostIp;
    private String podIp;
    private String phase;
    private String reason;
    private String startTime;
    private Boolean hasJolokia;
    private List<String> containers;

    public PodStatus() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getPodIp() {
        return podIp;
    }

    public void setPodIp(String podIp) {
        this.podIp = podIp;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Boolean getHasJolokia() {
        return hasJolokia;
    }

    public void setHasJolokia(Boolean hasJolokia) {
        this.hasJolokia = hasJolokia;
    }

    public List<String> getContainers() {
        return containers;
    }

    public void setContainers(List<String> containers) {
        this.containers = containers;
    }
}
