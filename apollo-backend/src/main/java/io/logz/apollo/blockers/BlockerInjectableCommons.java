package io.logz.apollo.blockers;

import io.logz.apollo.scm.GithubConnector;

import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 6/4/17.
 */
public class BlockerInjectableCommons {

    private final GithubConnector githubConnector;

    @Inject
    public BlockerInjectableCommons(GithubConnector githubConnector) {
        this.githubConnector = requireNonNull(githubConnector);
    }

    public GithubConnector getGithubConnector() {
        return githubConnector;
    }
}
