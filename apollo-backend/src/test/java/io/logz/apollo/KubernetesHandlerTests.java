package io.logz.apollo;

import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodListBuilder;
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.extensions.DeploymentListBuilder;
import io.fabric8.kubernetes.api.model.extensions.DeploymentStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.mock.KubernetesMockClient;
import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.RealDeploymentGenerator;
import io.logz.apollo.helpers.StandaloneApollo;
import io.logz.apollo.kubernetes.ApolloToKubernetes;
import io.logz.apollo.kubernetes.ApolloToKubernetesFactory;
import io.logz.apollo.kubernetes.KubernetesHandler;
import io.logz.apollo.kubernetes.KubernetesHandlerFactory;
import io.logz.apollo.models.Deployment;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by roiravhon on 2/7/17.
 */
public class KubernetesHandlerTests {

    private final String LOG_MESSAGE_IN_POD = "test log message to search..";

    private final DeploymentDao deploymentDao;
    private final KubernetesMockClient kubernetesMockClient;
    private final RealDeploymentGenerator notFinishedDeployment;
    private final RealDeploymentGenerator finishedDeployment;
    private final RealDeploymentGenerator notFinishedCanceledDeployment;
    private final RealDeploymentGenerator finishedCanceledDeployment;

    private final KubernetesHandler notFinishedDeploymentHandler;

    public KubernetesHandlerTests() throws ScriptException, IOException, SQLException {

        // We need a server here
        StandaloneApollo.getOrCreateServer();

        kubernetesMockClient = new KubernetesMockClient();
        deploymentDao = ApolloMyBatis.getDao(DeploymentDao.class);

        // Create deployments
        notFinishedDeployment = new RealDeploymentGenerator("image", "key", "value");
        finishedDeployment = new RealDeploymentGenerator("image", "key", "value");
        notFinishedCanceledDeployment = new RealDeploymentGenerator("image", "key", "value");
        finishedCanceledDeployment = new RealDeploymentGenerator("image", "key", "value");

        // Set mock endpoints
        setMockDeploymentStatus(notFinishedDeployment, false, ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(notFinishedDeployment.getDeployment()));
        setMockDeploymentStatus(finishedDeployment, true, ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(finishedDeployment.getDeployment()));
        setMockDeploymentStatus(notFinishedCanceledDeployment, false, ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(notFinishedCanceledDeployment.getDeployment()));
        setMockDeploymentStatus(finishedCanceledDeployment, true, ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(finishedCanceledDeployment.getDeployment()));

        // Set the logs mock on arbitrary one
        setMockPodLogs(notFinishedDeployment, LOG_MESSAGE_IN_POD, ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(notFinishedDeployment.getDeployment()));

        // Get an instance of the client
        KubernetesClient kubernetesClient = kubernetesMockClient.replay();

        // Inject the client
        notFinishedDeploymentHandler = KubernetesHandlerFactory.getOrCreateKubernetesHandlerWithSpecificClient(notFinishedDeployment.getEnvironment(), kubernetesClient);
        KubernetesHandlerFactory.getOrCreateKubernetesHandlerWithSpecificClient(finishedDeployment.getEnvironment(), kubernetesClient);
        KubernetesHandlerFactory.getOrCreateKubernetesHandlerWithSpecificClient(notFinishedCanceledDeployment.getEnvironment(), kubernetesClient);
        KubernetesHandlerFactory.getOrCreateKubernetesHandlerWithSpecificClient(finishedCanceledDeployment.getEnvironment(), kubernetesClient);

        // Since the mock library does not support "createOrReplace" we can't mock this phase (and its fine to neglect it since its fabric8 code)
        notFinishedDeployment.updateDeploymentStatus(Deployment.DeploymentStatus.STARTED);
        finishedDeployment.updateDeploymentStatus(Deployment.DeploymentStatus.STARTED);
        notFinishedCanceledDeployment.updateDeploymentStatus(Deployment.DeploymentStatus.CANCELING);
        finishedCanceledDeployment.updateDeploymentStatus(Deployment.DeploymentStatus.CANCELING);

        // TODO: This can cause test concurrency issues in case we will want to run this in parallel. In the current singleton nature of those tests, no other way unfortunately
        StandaloneApollo.getOrCreateServer().startKubernetesMonitor();
    }

    @Test
    public void testDeploymentMonitor() {

        // We need to wait at least 2 iterations of the monitoring thread + 1 buffer
        Common.waitABit(3);

        Deployment currentNotFinishedDeployment = deploymentDao.getDeployment(notFinishedDeployment.getDeployment().getId());
        Deployment currentFinishedDeployment = deploymentDao.getDeployment(finishedDeployment.getDeployment().getId());
        Deployment currentNotFinishedCanceledDeployment = deploymentDao.getDeployment(notFinishedCanceledDeployment.getDeployment().getId());
        Deployment currentFinishedCanceledDeployment = deploymentDao.getDeployment(finishedCanceledDeployment.getDeployment().getId());

        assertThat(currentNotFinishedDeployment.getStatus()).isEqualTo(Deployment.DeploymentStatus.STARTED);
        assertThat(currentFinishedDeployment.getStatus()).isEqualTo(Deployment.DeploymentStatus.DONE);
        assertThat(currentNotFinishedCanceledDeployment.getStatus()).isEqualTo(Deployment.DeploymentStatus.CANCELING);
        assertThat(currentFinishedCanceledDeployment.getStatus()).isEqualTo(Deployment.DeploymentStatus.CANCELED);
    }

    @Test
    public void testGetLogs() {

        String logs = notFinishedDeploymentHandler.getDeploymentLogs(notFinishedDeployment.getDeployment());
        assertThat(logs).contains(LOG_MESSAGE_IN_POD);
    }

    private void setMockDeploymentStatus(RealDeploymentGenerator realDeploymentGenerator, boolean finished, ApolloToKubernetes apolloToKubernetes) {

        DeploymentStatus deploymentStatus;
        if (finished) {
            deploymentStatus = new DeploymentStatus(1, 1L, 1, 0, 1);
        } else {
            deploymentStatus = new DeploymentStatus(1, 1L, 1, 0, 0);
        }

        kubernetesMockClient
                .extensions()
                .deployments()
                .inNamespace(realDeploymentGenerator.getEnvironment().getKubernetesNamespace())
                .withLabel(ApolloToKubernetes.getApolloDeploymentUniqueIdentifierKey(), apolloToKubernetes.getApolloDeploymentUniqueIdentifierValue())
                .list()
                .andReturn(
                        new DeploymentListBuilder()
                                .withItems(
                                        new DeploymentBuilder()
                                                .withStatus(deploymentStatus)
                                                .build()
                                ).build()
                ).anyTimes();
    }

    private void setMockPodLogs(RealDeploymentGenerator realDeploymentGenerator, String log, ApolloToKubernetes apolloToKubernetes) {

        String podName = "mavet-pod-" + Common.randomStr(5);

        kubernetesMockClient
                .pods()
                .inNamespace(realDeploymentGenerator.getEnvironment().getKubernetesNamespace())
                .withLabel(ApolloToKubernetes.getApolloDeploymentUniqueIdentifierKey(), apolloToKubernetes.getApolloDeploymentUniqueIdentifierValue())
                .list()
                .andReturn(
                        new PodListBuilder()
                                .withItems(
                                        new PodBuilder()
                                                .withNewMetadata()
                                                    .withName(podName)
                                                .endMetadata()
                                        .build()

                                )
                        .build()
                ).anyTimes();

        kubernetesMockClient
                .pods()
                .inNamespace(realDeploymentGenerator.getEnvironment().getKubernetesNamespace())
                .withName(podName)
                .getLog(true)
                .andReturn(log)
                .anyTimes();
    }
}
