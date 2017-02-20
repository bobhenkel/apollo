package io.logz.apollo.scm;

import java.util.Date;

/**
 * Created by roiravhon on 2/20/17.
 */
public class CommitDetails {

    private final String sha;
    private final String commitUrl;
    private final String commitMessage;
    private final Date commitDate;
    private final String committerAvatarUrl;
    private final String committerName;

    public CommitDetails(String sha, String commitUrl, String commitMessage, Date commitDate, String committerAvatarUrl, String committerName) {
        this.sha = sha;
        this.commitUrl = commitUrl;
        this.commitMessage = commitMessage;
        this.commitDate = commitDate;
        this.committerAvatarUrl = committerAvatarUrl;
        this.committerName = committerName;
    }

    public String getSha() {
        return sha;
    }

    public String getCommitUrl() {
        return commitUrl;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public String getCommitterAvatarUrl() {
        return committerAvatarUrl;
    }

    public String getCommitterName() {
        return committerName;
    }
}
