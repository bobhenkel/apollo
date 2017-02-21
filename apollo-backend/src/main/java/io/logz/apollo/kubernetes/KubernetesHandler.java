package io.logz.apollo.kubernetes;

import com.google.common.annotations.VisibleForTesting;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Created by roiravhon on 2/2/17.
 */
public class KubernetesHandler {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesHandler.class);
    private final Environment environment;
    private final KubernetesClient kubernetesClient;

    @VisibleForTesting
    KubernetesHandler(Environment environment, KubernetesClient kubernetesClient) {
        this.environment = environment;
        this.kubernetesClient = kubernetesClient;
    }

    KubernetesHandler(Environment environment) {
        try {
            this.environment = environment;

            Config config = new ConfigBuilder()
                    .withMasterUrl(environment.getKubernetesMaster())
                    .withOauthToken(environment.getKubernetesToken())
                    .build();

            kubernetesClient = new DefaultKubernetesClient(config);

        } catch (Exception e) {
            logger.error("Could not create kubernetes client for environment {}", environment.getId(), e);
            throw new RuntimeException();
        }
    }

    Deployment startDeployment(Deployment deployment) {
        try {
            ApolloToKubernetes apolloToKubernetes = ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(deployment);
            io.fabric8.kubernetes.api.model.extensions.Deployment kubernetesDeployment = apolloToKubernetes.getKubernetesDeployment();
            io.fabric8.kubernetes.api.model.Service kubernetesService = apolloToKubernetes.getKubernetesService();

            kubernetesClient
                    .extensions()
                    .deployments()
                    .inNamespace(environment.getKubernetesNamespace())
                    .createOrReplace(kubernetesDeployment);

            kubernetesClient
                    .services()
                    .inNamespace(environment.getKubernetesNamespace())
                    .createOrReplace(kubernetesService);

            logger.info("Started deployment id {}", deployment.getId());
            deployment.setStatus(Deployment.DeploymentStatus.STARTED);
            return deployment;

        } catch (Exception e) {
            logger.error("Got exception while deploying to kubernetes deployment id {}. Leaving in its original state", deployment.getId(), e);
            return deployment;
        }
    }

    Deployment cancelDeployment(Deployment deployment) {
        try {
            ApolloToKubernetes apolloToKubernetes = ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(deployment);
            io.fabric8.kubernetes.api.model.extensions.Deployment kubernetesDeployment = apolloToKubernetes.getKubernetesDeployment();
            kubernetesClient
                    .extensions()
                    .deployments()
                    .inNamespace(environment.getKubernetesNamespace())
                    .createOrReplace(kubernetesDeployment);

            logger.info("Canceled deployment id {}", deployment.getId());
            deployment.setStatus(Deployment.DeploymentStatus.CANCELING);
            return deployment;

        } catch (Exception e) {
            logger.error("Got exception while canceling deployment id {}. Leaving in its original state", deployment.getId(), e);
            return deployment;
        }
    }

    Deployment monitorDeployment(Deployment deployment) {

        try {
            ApolloToKubernetes apolloToKubernetes = ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(deployment);
            Optional<io.fabric8.kubernetes.api.model.extensions.Deployment> returnedDeployment = kubernetesClient
                    .extensions()
                    .deployments()
                    .inNamespace(environment.getKubernetesNamespace())
                    .withLabel(ApolloToKubernetes.getApolloDeploymentUniqueIdentifierKey(), apolloToKubernetes.getApolloDeploymentUniqueIdentifierValue())
                    .list()
                    .getItems()
                    .stream()
                    .findFirst();

            if (returnedDeployment.isPresent()) {
                io.fabric8.kubernetes.api.model.extensions.DeploymentStatus deploymentStatus = returnedDeployment.get().getStatus();

                int updatedReplicas = deploymentStatus.getUpdatedReplicas();
                int totalReplicas = deploymentStatus.getReplicas();

                logger.info("Monitoring of deployment id {}: out of {} replicas, {} are updated", deployment.getId(), totalReplicas, updatedReplicas);
                if (updatedReplicas == totalReplicas) {
                    if (deployment.getStatus().equals(Deployment.DeploymentStatus.STARTED)) {
                        logger.info("Deployment id {} is done deploying", deployment.getId());
                        deployment.setStatus(Deployment.DeploymentStatus.DONE);

                    } else if (deployment.getStatus().equals(Deployment.DeploymentStatus.CANCELING)) {
                        logger.info("Deployment id {} is done canceling", deployment.getId());
                        deployment.setStatus(Deployment.DeploymentStatus.CANCELED);
                    }
                }

            } else {
                logger.warn("Found no deployments in kubernetes matching apollo_unique_identifier={}", apolloToKubernetes.getApolloDeploymentUniqueIdentifierValue());
            }

            return deployment;

        } catch (Exception e) {
            logger.error("Got exception while monitoring deployment {}. Leaving it in its original state", deployment.getId(), e);
            return deployment;
        }
    }

    public String getDeploymentLogs(Deployment deployment) {

        try {
            ApolloToKubernetes apolloToKubernetes = ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(deployment);
            StringBuilder sb = new StringBuilder();
            kubernetesClient
                    .pods()
                    .inNamespace(environment.getKubernetesNamespace())
                    .withLabel(ApolloToKubernetes.getApolloDeploymentUniqueIdentifierKey(), apolloToKubernetes.getApolloDeploymentPodUniqueIdentifierValue())
                    .list()
                    .getItems()
                    .stream()
                    .map(pod -> pod.getMetadata().getName())
                    .forEach(name -> {
                        sb.append("Logs from ").append(name).append(":\n")
                                .append(kubernetesClient.pods().inNamespace(environment.getKubernetesNamespace()).withName(name).getLog(true))
                                .append("\n");
                    });

            return sb.toString();
        } catch (Exception e) {
            logger.error("Got exception while getting logs for deployment {}", deployment.getId());
            return "Can't get logs!";
        }
    }
}
