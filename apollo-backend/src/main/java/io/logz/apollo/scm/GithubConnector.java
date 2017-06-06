package io.logz.apollo.scm;

import io.logz.apollo.configuration.ApolloConfiguration;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by roiravhon on 2/20/17.
 */
@Singleton
public class GithubConnector {

    private static final Logger logger = LoggerFactory.getLogger(GithubConnector.class);

    private final GitHub gitHub;

    @Inject
    public GithubConnector(ApolloConfiguration apolloConfiguration) {
        try {
            logger.info("Initializing Github Connector");

            // If no user or oauth was provided, attempt to go anonymous
            if (StringUtils.isEmpty(apolloConfiguration.getGithubLogin()) || StringUtils.isEmpty(apolloConfiguration.getGithubOauthToken())) {
                gitHub = GitHub.connectAnonymously();
            } else {
                gitHub = GitHub.connect(apolloConfiguration.getGithubLogin(), apolloConfiguration.getGithubOauthToken());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not open connection to Github!", e);
        }
    }

    public Optional<CommitDetails> getCommitDetails(String githubRepo, String sha) {
        try {
            logger.info("Getting commit details for sha {} on url {}", sha, githubRepo);
            GHCommit commit = gitHub.getRepository(githubRepo).getCommit(sha);

            GHUser author = commit.getAuthor();
            String committerName = author.getName();
            if (committerName == null || committerName.isEmpty()) {
                committerName = author.getLogin();
            }

            CommitDetails commitDetails = new CommitDetails(sha, commit.getHtmlUrl().toString(),
                    commit.getCommitShortInfo().getMessage(), commit.getCommitDate(),
                    author.getAvatarUrl(), committerName);
            return Optional.of(commitDetails);
        } catch (IOException e) {
            logger.warn("Could not get commit details from Github!", e);
            return Optional.empty();
        }
    }

    public Optional<String> getLatestCommitShaOnBranch(String githubRepo, String branchName) {
        try {
            return Optional.of(gitHub.getRepository(githubRepo).getBranch(branchName).getSHA1());
        } catch (Exception e) {
            logger.warn("Could not get latest commit on branch from Github!", e);
            return Optional.empty();
        }
    }

    public boolean isCommitInBranchHistory(String githubRepo, String branch, String sha) {
        Optional<List<GHCommit>> allCommitsOnABranch = getAllCommitsOnABranch(githubRepo, branch);
        return allCommitsOnABranch.map(ghCommits -> ghCommits
                .stream()
                .anyMatch(ghCommit -> ghCommit.getSHA1().equals(sha)))
                .orElse(false);
    }

    public static String getRepoNameFromRepositoryUrl(String githubRepositoryUrl) {
        return githubRepositoryUrl.replaceFirst("https?://github.com/", "");
    }

    private Optional<List<GHCommit>> getAllCommitsOnABranch(String githubRepo, String branch) {
        try {
            return Optional.of(gitHub.getRepository(githubRepo).queryCommits().from(branch).list().asList());
        } catch (Throwable e) {  // The library is throwing and Error and not an exception, for god sake
            logger.warn("Could not get all commits on branch {} for repo {}", branch, githubRepo, e);
            return Optional.empty();
        }
    }
}
