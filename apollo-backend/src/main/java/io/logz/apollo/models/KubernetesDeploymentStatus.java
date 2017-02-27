package io.logz.apollo.models;

import java.util.List;

/**
 * Created by roiravhon on 2/22/17.
 */
public class KubernetesDeploymentStatus {

    private int serviceId;
    private int environmentId;
    private String gitCommitSha;
    private int replicas;
    private int availableReplicas;
    private int updatedReplicas;
    private int unavailableReplicas;
    private List<PodStatus> podStatuses;

    public KubernetesDeploymentStatus() {
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(int environmentId) {
        this.environmentId = environmentId;
    }

    public String getGitCommitSha() {
        return gitCommitSha;
    }

    public void setGitCommitSha(String gitCommitSha) {
        this.gitCommitSha = gitCommitSha;
    }

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }

    public int getAvailableReplicas() {
        return availableReplicas;
    }

    public void setAvailableReplicas(int availableReplicas) {
        this.availableReplicas = availableReplicas;
    }

    public int getUpdatedReplicas() {
        return updatedReplicas;
    }

    public void setUpdatedReplicas(int updatedReplicas) {
        this.updatedReplicas = updatedReplicas;
    }

    public int getUnavailableReplicas() {
        return unavailableReplicas;
    }

    public void setUnavailableReplicas(int unavailableReplicas) {
        this.unavailableReplicas = unavailableReplicas;
    }

    public List<PodStatus> getPodStatuses() {
        return podStatuses;
    }

    public void setPodStatuses(List<PodStatus> podStatuses) {
        this.podStatuses = podStatuses;
    }
}
