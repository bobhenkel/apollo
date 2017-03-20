package io.logz.apollo;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodListBuilder;
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.extensions.DeploymentListBuilder;
import io.fabric8.kubernetes.api.model.extensions.DeploymentStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.mock.KubernetesMockClient;
import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.database.ApolloMyBatis.ApolloMyBatisSession;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.ModelsGenerator;
import io.logz.apollo.helpers.RealDeploymentGenerator;
import io.logz.apollo.helpers.StandaloneApollo;
import io.logz.apollo.kubernetes.ApolloToKubernetes;
import io.logz.apollo.kubernetes.ApolloToKubernetesFactory;
import io.logz.apollo.kubernetes.KubernetesHandler;
import io.logz.apollo.kubernetes.KubernetesHandlerFactory;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.KubernetesDeploymentStatus;
import io.logz.apollo.models.PodStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import javax.script.ScriptException;
import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by roiravhon on 2/7/17.
 */
public class KubernetesHandlerTest {

    private static final String LOG_MESSAGE_IN_POD = "test log message to search..";

    private static KubernetesMockClient kubernetesMockClient;
    private static RealDeploymentGenerator notFinishedDeployment;
    private static RealDeploymentGenerator finishedDeployment;
    private static RealDeploymentGenerator notFinishedCanceledDeployment;
    private static RealDeploymentGenerator finishedCanceledDeployment;
    private static RealDeploymentGenerator statusDeployment;
    private static PodStatus podStatus;

    private static KubernetesHandler notFinishedDeploymentHandler;

    @BeforeClass
    public static void initialize() throws ScriptException, IOException, SQLException {

        // We need a server here
        StandaloneApollo.getOrCreateServer();

        kubernetesMockClient = new KubernetesMockClient();

        // Create deployments
        notFinishedDeployment = new RealDeploymentGenerator("image", "key", "value");
        finishedDeployment = new RealDeploymentGenerator("image", "key", "value");
        notFinishedCanceledDeployment = new RealDeploymentGenerator("image", "key", "value");
        finishedCanceledDeployment = new RealDeploymentGenerator("image", "key", "value");
        statusDeployment = new RealDeploymentGenerator("image", "key", "value");

        // Set mock endpoints
        setMockDeploymentStatus(notFinishedDeployment, false, ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(notFinishedDeployment.getDeployment()));
        setMockDeploymentStatus(finishedDeployment, true, ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(finishedDeployment.getDeployment()));
        setMockDeploymentStatus(notFinishedCanceledDeployment, false, ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(notFinishedCanceledDeployment.getDeployment()));
        setMockDeploymentStatus(finishedCanceledDeployment, true, ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(finishedCanceledDeployment.getDeployment()));
        setMockDeploymentStatus(statusDeployment, true, ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(statusDeployment.getDeployment()));

        // Setting a mock pod status
        podStatus = new PodStatus();
        podStatus.setName("mavet-pod-" + Common.randomStr(5));
        podStatus.setHostIp("1.1.1.1");
        podStatus.setPodIp("2.2.2.2");
        podStatus.setPhase("Dying");
        podStatus.setReason("Kaha");
        podStatus.setStartTime("Beginning of humanity");


        // Set the logs and status mock on arbitrary one
        setMockPodLogsAndStatus(notFinishedDeployment, LOG_MESSAGE_IN_POD, podStatus, ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(notFinishedDeployment.getDeployment()));

        // And we need the mocks on the pod level again on the status check mock
        setMockPodLogsAndStatus(statusDeployment, LOG_MESSAGE_IN_POD, podStatus, ApolloToKubernetesFactory.getOrCreateApolloToKubernetes(statusDeployment.getDeployment()));

        // Get an instance of the client
        KubernetesClient kubernetesClient = kubernetesMockClient.replay();

        // Inject the client
        notFinishedDeploymentHandler = KubernetesHandlerFactory.getOrCreateKubernetesHandlerWithSpecificClient(notFinishedDeployment.getEnvironment(), kubernetesClient);
        KubernetesHandlerFactory.getOrCreateKubernetesHandlerWithSpecificClient(finishedDeployment.getEnvironment(), kubernetesClient);
        KubernetesHandlerFactory.getOrCreateKubernetesHandlerWithSpecificClient(notFinishedCanceledDeployment.getEnvironment(), kubernetesClient);
        KubernetesHandlerFactory.getOrCreateKubernetesHandlerWithSpecificClient(finishedCanceledDeployment.getEnvironment(), kubernetesClient);
        KubernetesHandlerFactory.getOrCreateKubernetesHandlerWithSpecificClient(statusDeployment.getEnvironment(), kubernetesClient);

        // Since the mock library does not support "createOrReplace" we can't mock this phase (and its fine to neglect it since its fabric8 code)
        notFinishedDeployment.updateDeploymentStatus(Deployment.DeploymentStatus.STARTED);
        finishedDeployment.updateDeploymentStatus(Deployment.DeploymentStatus.STARTED);
        notFinishedCanceledDeployment.updateDeploymentStatus(Deployment.DeploymentStatus.CANCELING);
        finishedCanceledDeployment.updateDeploymentStatus(Deployment.DeploymentStatus.CANCELING);
        statusDeployment.updateDeploymentStatus(Deployment.DeploymentStatus.DONE);

        // TODO: This can cause test concurrency issues in case we will want to run this in parallel. In the current singleton nature of those tests, no other way unfortunately
        StandaloneApollo.getOrCreateServer().startKubernetesMonitor();
    }

    @Test
    public void testDeploymentMonitor() {

        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentDao deploymentDao = apolloMyBatisSession.getDao(DeploymentDao.class);

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
    }

    @Test
    public void testGetLogs() {

        String logs = notFinishedDeploymentHandler.getDeploymentLogs(notFinishedDeployment.getDeployment());
        assertThat(logs).contains(LOG_MESSAGE_IN_POD);
    }

    @Test
    public void testGetStatus() {

        KubernetesDeploymentStatus returnedDeploymentStatus = notFinishedDeploymentHandler.getCurrentStatus(notFinishedDeployment.getService());
        PodStatus returnedPodStatus = returnedDeploymentStatus.getPodStatuses().stream().findFirst().orElse(null);

        assertThat(returnedPodStatus).isNotNull();

        assertThat(returnedDeploymentStatus.getAvailableReplicas()).isEqualTo(1);
        assertThat(returnedDeploymentStatus.getReplicas()).isEqualTo(1);
        assertThat(returnedDeploymentStatus.getUnavailableReplicas()).isEqualTo(0);
        assertThat(returnedDeploymentStatus.getUpdatedReplicas()).isEqualTo(0);
        assertThat(returnedPodStatus.getName()).isEqualTo(podStatus.getName());
        assertThat(returnedPodStatus.getHostIp()).isEqualTo(podStatus.getHostIp());
        assertThat(returnedPodStatus.getPodIp()).isEqualTo(podStatus.getPodIp());
        assertThat(returnedPodStatus.getPhase()).isEqualTo(podStatus.getPhase());
        assertThat(returnedPodStatus.getReason()).isEqualTo(podStatus.getReason());
        assertThat(returnedPodStatus.getStartTime()).isEqualTo(podStatus.getStartTime());
    }

    @Test
    public void testNewDeploymentGetLatestShaOnNewDeployment() throws Exception {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();
        Common.grantUserFullPermissionsOnEnvironment(apolloTestClient, statusDeployment.getEnvironment());

        DeployableVersion deployableVersion = ModelsGenerator.createDeployableVersion(statusDeployment.getService());
        deployableVersion.setId(apolloTestClient.addDeployableVersion(deployableVersion).getId());

        Deployment restDeployment = ModelsGenerator.createDeployment(statusDeployment.getService(),
                                                                     statusDeployment.getEnvironment(),
                                                                     deployableVersion);

        restDeployment.setId(apolloTestClient.addDeployment(restDeployment).getId());

        assertThat(apolloTestClient.getDeployment(restDeployment.getId()).getSourceVersion())
                .isEqualTo(statusDeployment.getDeployableVersion().getGitCommitSha());
    }

    private static void setMockDeploymentStatus(RealDeploymentGenerator realDeploymentGenerator, boolean finished, ApolloToKubernetes apolloToKubernetes) {

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
                                                .withNewMetadata()
                                                .withLabels(
                                                        ImmutableMap.of(ApolloToKubernetes.getApolloCommitShaKey(),
                                                                realDeploymentGenerator.getDeployableVersion().getGitCommitSha())
                                                )
                                                .endMetadata()
                                                .build()
                                ).build()
                ).anyTimes();
    }

    private static void setMockPodLogsAndStatus(RealDeploymentGenerator realDeploymentGenerator,
                                                String log, PodStatus podStatus, ApolloToKubernetes apolloToKubernetes) {

        Pod pod = new PodBuilder()
                        .withNewMetadata()
                            .withName(podStatus.getName())
                        .endMetadata()
                        .withNewStatus()
                            .withHostIP(podStatus.getHostIp())
                            .withPodIP(podStatus.getPodIp())
                            .withPhase(podStatus.getPhase())
                            .withReason(podStatus.getReason())
                            .withStartTime(podStatus.getStartTime())
                        .endStatus()
                        .build();

        kubernetesMockClient
                .pods()
                .inNamespace(realDeploymentGenerator.getEnvironment().getKubernetesNamespace())
                .withLabel(ApolloToKubernetes.getApolloDeploymentUniqueIdentifierKey(), apolloToKubernetes.getApolloDeploymentPodUniqueIdentifierValue())
                .list()
                .andReturn(
                        new PodListBuilder()
                                .withItems(pod)
                        .build()
                ).anyTimes();

        kubernetesMockClient
                .pods()
                .inNamespace(realDeploymentGenerator.getEnvironment().getKubernetesNamespace())
                .withName(podStatus.getName())
                .getLog(true)
                .andReturn(log)
                .anyTimes();

        kubernetesMockClient
                .pods()
                .inNamespace(realDeploymentGenerator.getEnvironment().getKubernetesNamespace())
                .withName(podStatus.getName())
                .get()
                .andReturn(pod)
                .anyTimes();
    }
}
