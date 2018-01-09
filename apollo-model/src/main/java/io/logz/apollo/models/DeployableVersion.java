package io.logz.apollo.models;

import java.util.Date;

/**
 * Created by roiravhon on 12/20/16.
 */
public class DeployableVersion {

    private int id;
    private String gitCommitSha;
    private String githubRepositoryUrl;
    private int serviceId;
    private String commitUrl;
    private String commitMessage;
    private Date commitDate;
    private String committerAvatarUrl;
    private String committerName;

    public DeployableVersion() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGitCommitSha() {
        return gitCommitSha;
    }

    public void setGitCommitSha(String gitCommitSha) {
        this.gitCommitSha = gitCommitSha;
    }

    public String getGithubRepositoryUrl() {
        return githubRepositoryUrl;
    }

    public void setGithubRepositoryUrl(String githubRepositoryUrl) {
        this.githubRepositoryUrl = githubRepositoryUrl;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getCommitUrl() {
        return commitUrl;
    }

    public void setCommitUrl(String commitUrl) {
        this.commitUrl = commitUrl;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

    public String getCommitterAvatarUrl() {
        return committerAvatarUrl;
    }

    public void setCommitterAvatarUrl(String committerAvatarUrl) {
        this.committerAvatarUrl = committerAvatarUrl;
    }

    public String getCommitterName() {
        return committerName;
    }

    public void setCommitterName(String committerName) {
        this.committerName = committerName;
    }
}
