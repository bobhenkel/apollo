package io.logz.apollo.blockers;

import io.logz.apollo.dao.BlockerDefinitionDao;
import io.logz.apollo.dao.DeployableVersionDao;
import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.scm.GithubConnector;

import javax.inject.Inject;
import javax.inject.Singleton;

import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 6/4/17.
 */
@Singleton
public class BlockerInjectableCommons {

    private final GithubConnector githubConnector;
    private final BlockerDefinitionDao blockerDefinitionDao;
    private final DeployableVersionDao deployableVersionDao;
    private final DeploymentDao deploymentDao;

    @Inject
    public BlockerInjectableCommons(GithubConnector githubConnector, BlockerDefinitionDao blockerDefinitionDao, DeployableVersionDao deployableVersionDao, DeploymentDao deploymentDao) {
        this.githubConnector = requireNonNull(githubConnector);
        this.blockerDefinitionDao = requireNonNull(blockerDefinitionDao);
        this.deployableVersionDao = requireNonNull(deployableVersionDao);
        this.deploymentDao = requireNonNull(deploymentDao);
    }

    public GithubConnector getGithubConnector() {
        return githubConnector;
    }

    public BlockerDefinitionDao getBlockerDefinitionDao() { return blockerDefinitionDao; }

    public DeployableVersionDao getDeployableVersionDao() { return deployableVersionDao; }

    public DeploymentDao getDeploymentDao() { return deploymentDao; }
}
