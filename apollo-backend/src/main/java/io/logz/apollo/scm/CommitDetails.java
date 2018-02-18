package io.logz.apollo.scm;

import org.kohsuke.github.GHCommitStatus;

import java.util.Date;

/**
 * Created by roiravhon on 2/20/17.
 */
public class CommitDetails {

    private final String sha;
    private final String commitUrl;
    private final String commitMessage;
    private final Date commitDate;
    private final GHCommitStatus commitStatus;
    private final String committerAvatarUrl;
    private final String committerName;

    public CommitDetails(String sha, String commitUrl, String commitMessage, Date commitDate, GHCommitStatus commitStatus, String committerAvatarUrl, String committerName) {
        this.sha = sha;
        this.commitUrl = commitUrl;
        this.commitMessage = commitMessage;
        this.commitDate = commitDate;
        this.commitStatus = commitStatus;
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

    public GHCommitStatus getCommitStatus() { return commitStatus; }

    public String getCommitterAvatarUrl() {
        return committerAvatarUrl;
    }

    public String getCommitterName() {
        return committerName;
    }
}
