package io.logz.apollo.models;

/**
 * Created by roiravhon on 12/20/16.
 */
public class DeployableVersion {

    private int id;
    private String gitCommitSha;
    private String githubRepositoryUrl;
    private int relatedService;

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

    public int getRelatedService() {
        return relatedService;
    }

    public void setRelatedService(int relatedService) {
        this.relatedService = relatedService;
    }
}
