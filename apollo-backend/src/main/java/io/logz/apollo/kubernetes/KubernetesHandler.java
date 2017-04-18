package io.logz.apollo.kubernetes;

import com.google.common.annotations.VisibleForTesting;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.PodStatus;
import io.logz.apollo.models.Service;
import io.logz.apollo.models.KubernetesDeploymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by roiravhon on 2/2/17.
 */
public class KubernetesHandler {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesHandler.class);
    private final Environment environment;
    private static final int NUMBER_OF_LOG_LINES_TO_FETCH = 500;
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

    public Deployment startDeployment(Deployment deployment) {
        try {
            ApolloToKubernetes apolloToKubernetes = ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(deployment);
            io.fabric8.kubernetes.api.model.extensions.Deployment kubernetesDeployment = apolloToKubernetes.getKubernetesDeployment();
            io.fabric8.kubernetes.api.model.Service kubernetesService = apolloToKubernetes.getKubernetesService();

            kubernetesClient
                    .extensions()
                    .deployments()
                    .inNamespace(environment.getKubernetesNamespace())
                    .createOrReplace(kubernetesDeployment);

            // Services are allowed to be null
            if (kubernetesService != null) {
                kubernetesClient
                        .services()
                        .inNamespace(environment.getKubernetesNamespace())
                        .createOrReplace(kubernetesService);
            }

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

    public String getDeploymentLogs(Environment environment, Service service) {

        try {
            StringBuilder sb = new StringBuilder();
            kubernetesClient
                    .pods()
                    .inNamespace(environment.getKubernetesNamespace())
                    .withLabel(ApolloToKubernetes.getApolloDeploymentUniqueIdentifierKey(), ApolloToKubernetes.getApolloPodUniqueIdentifier(environment, service))
                    .list()
                    .getItems()
                    .stream()
                    .map(pod -> pod.getMetadata().getName())
                    .forEach(podName -> kubernetesClient
                            .pods()
                            .inNamespace(environment.getKubernetesNamespace())
                            .withName(podName)
                            .get()
                            .getSpec()
                            .getContainers()
                            .forEach(container ->
                                    sb.append("Logs from container ").append(container.getName()).append(" on pod ").append(podName).append(": \n").append(
                                            kubernetesClient
                                            .pods()
                                            .inNamespace(environment.getKubernetesNamespace())
                                            .withName(podName)
                                            .inContainer(container.getName())
                                            .tailingLines(NUMBER_OF_LOG_LINES_TO_FETCH)
                                            .getLog(true)
                                    ).append("\n")));

            return sb.toString();
        } catch (Exception e) {
            logger.error("Got exception while getting logs for service {} on environment {}", service.getId(), environment.getId(), e);
            return "Can't get logs!";
        }
    }

    public KubernetesDeploymentStatus getCurrentStatus(Service service) {

        io.fabric8.kubernetes.api.model.extensions.Deployment deployment = kubernetesClient
                .extensions()
                .deployments()
                .inNamespace(environment.getKubernetesNamespace())
                .withLabel(ApolloToKubernetes.getApolloDeploymentUniqueIdentifierKey(), ApolloToKubernetes.getApolloDeploymentUniqueIdentifierValue(environment, service))
                .list()
                .getItems()
                .stream()
                .findFirst()
                .orElse(null);

        if (deployment == null) {
            logger.warn("Could not find deployment for environment {} and service {}, can't return the status!", environment.getId(), service.getId());
            return null;
        }

        List<PodStatus> podStatusList = kubernetesClient
                .pods()
                .inNamespace(environment.getKubernetesNamespace())
                .withLabel(ApolloToKubernetes.getApolloDeploymentUniqueIdentifierKey(), ApolloToKubernetes.getApolloPodUniqueIdentifier(environment, service))
                .list()
                .getItems()
                .stream()
                .map(pod -> pod.getMetadata().getName())
                .map(this::getPodStatus)
                .collect(Collectors.toList());

        KubernetesDeploymentStatus kubernetesDeploymentStatus = new KubernetesDeploymentStatus();
        kubernetesDeploymentStatus.setServiceId(service.getId());
        kubernetesDeploymentStatus.setEnvironmentId(environment.getId());
        kubernetesDeploymentStatus.setGitCommitSha(deployment.getMetadata().getLabels().get(ApolloToKubernetes.getApolloCommitShaKey()));
        kubernetesDeploymentStatus.setReplicas(deployment.getStatus().getReplicas());
        kubernetesDeploymentStatus.setAvailableReplicas(deployment.getStatus().getAvailableReplicas());
        kubernetesDeploymentStatus.setUpdatedReplicas(deployment.getStatus().getUpdatedReplicas());
        kubernetesDeploymentStatus.setUnavailableReplicas(deployment.getStatus().getUnavailableReplicas());
        kubernetesDeploymentStatus.setPodStatuses(podStatusList);

        return kubernetesDeploymentStatus;
    }

    public void restartPod(String podName) {

        // Deleting a pod, which created with a deployment is basically restarting since the replica set will create a new one immediately
        kubernetesClient
                .pods()
                .inNamespace(environment.getKubernetesNamespace())
                .withName(podName)
                .delete();
    }

    private PodStatus getPodStatus(String name) {
        io.fabric8.kubernetes.api.model.PodStatus kubernetesPodStatus = kubernetesClient
                .pods()
                .inNamespace(environment.getKubernetesNamespace())
                .withName(name)
                .get()
                .getStatus();

        PodStatus podStatus = new PodStatus();
        podStatus.setName(name);
        podStatus.setHostIp(kubernetesPodStatus.getHostIP());
        podStatus.setPodIp(kubernetesPodStatus.getPodIP());
        podStatus.setPhase(kubernetesPodStatus.getPhase());
        podStatus.setReason(kubernetesPodStatus.getReason());
        podStatus.setStartTime(kubernetesPodStatus.getStartTime());

        return podStatus;
    }
}
