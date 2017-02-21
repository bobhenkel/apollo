package io.logz.apollo.scm;

import io.logz.apollo.configuration.ApolloConfiguration;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by roiravhon on 2/20/17.
 */
public class GithubConnector {

    private static final Logger logger = LoggerFactory.getLogger(GithubConnector.class);
    private static GithubConnector instance;
    private final GitHub gitHub;

    private GithubConnector(ApolloConfiguration apolloConfiguration) {
        try {
            // If no user or oauth was provided, attempt to go anonymous
            if (apolloConfiguration.getGithubLogin().equals("") || apolloConfiguration.getGithubOauthToken().equals("")) {
                gitHub = GitHub.connectAnonymously();
            } else {
                gitHub = GitHub.connect(apolloConfiguration.getGithubLogin(), apolloConfiguration.getGithubOauthToken());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not open connection to Github!", e);
        }
    }

    public static void initialize(ApolloConfiguration apolloConfiguration) {
        logger.info("Initializing Github Connector for the first time");
        instance = new GithubConnector(apolloConfiguration);
    }

    public static CommitDetails getCommitDetails(String githubRepo, String sha) {
        if (instance == null) {
            throw new RuntimeException("You must first initialize GithubConnector before getting commit details!");
        }
        try {
            logger.info("Getting commit details for sha {} on url {}", sha, githubRepo);
            GHCommit commit = instance.gitHub.getRepository(githubRepo).getCommit(sha);

            return new CommitDetails(sha, commit.getHtmlUrl().toString(),
                    commit.getCommitShortInfo().getMessage(), commit.getCommitDate(),
                    commit.getCommitter().getAvatarUrl(), commit.getCommitter().getName());

        } catch (IOException e) {
            logger.error("Could not get commit details from Github!", e);
            return null;
        }
    }
}
