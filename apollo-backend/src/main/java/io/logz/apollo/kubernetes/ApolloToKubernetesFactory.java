package io.logz.apollo.kubernetes;

import io.logz.apollo.dao.DeployableVersionDao;
import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 2/2/17.
 */
@Singleton
public class ApolloToKubernetesFactory {

    private final Map<Integer, ApolloToKubernetes> mappers;

    private final DeployableVersionDao deployableVersionDao;
    private final EnvironmentDao environmentDao;
    private final DeploymentDao deploymentDao;
    private final ServiceDao serviceDao;

    @Inject
    public ApolloToKubernetesFactory(DeployableVersionDao deployableVersionDao, EnvironmentDao environmentDao,
                                      DeploymentDao deploymentDao, ServiceDao serviceDao) {
        this.mappers = new ConcurrentHashMap<>();
        this.deployableVersionDao = requireNonNull(deployableVersionDao);
        this.environmentDao = requireNonNull(environmentDao);
        this.deploymentDao = requireNonNull(deploymentDao);
        this.serviceDao = requireNonNull(serviceDao);
    }

    public ApolloToKubernetes getOrCreateApolloToKubernetes(Deployment deployment) {
        return mappers.computeIfAbsent(deployment.getId(), key -> createMapper(deployment));
    }

    private ApolloToKubernetes createMapper(Deployment deployment) {
        DeployableVersion deployableVersion = deployableVersionDao.getDeployableVersion(deployment.getDeployableVersionId());
        Environment environment = environmentDao.getEnvironment(deployment.getEnvironmentId());
        Service service = serviceDao.getService(deployment.getServiceId());

        return new ApolloToKubernetes(deploymentDao, deployableVersion, environment, deployment, service);
    }
}
