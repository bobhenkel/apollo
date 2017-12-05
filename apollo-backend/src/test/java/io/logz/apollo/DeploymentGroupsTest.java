package io.logz.apollo;

import io.logz.apollo.kubernetes.ApolloToKubernetesStore;
import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.dao.DeployableVersionDao;
import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.RealDeploymentGenerator;
import io.logz.apollo.helpers.StandaloneApollo;
import io.logz.apollo.kubernetes.ApolloToKubernetes;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.HashMap;
import static org.assertj.core.api.Assertions.assertThat;


public class DeploymentGroupsTest {

    private static ApolloTestClient apolloTestClient;
    private static ApolloToKubernetesStore apolloToKubernetesStore;

    @BeforeClass
    public static void init() throws Exception {
        StandaloneApollo standaloneApollo = StandaloneApollo.getOrCreateServer();
        apolloTestClient = Common.signupAndLogin();
        apolloToKubernetesStore = standaloneApollo.getInstance(ApolloToKubernetesStore.class);
    }

    @Test
    public void testApolloDeploymentGroupYaml() throws Exception {
        RealDeploymentGenerator realDeploymentGenerator;
        ApolloToKubernetes apolloToKubernetes;

        HashMap<String, Object> params = new HashMap<>();

        params.put("image", "great image");
        params.put("key", "such key");
        params.put("value", "much value");

        realDeploymentGenerator = new RealDeploymentGenerator("{{ image }}", "{{ key }}", "{{ value }}", 0, new JSONObject(params).toString());
        Deployment deployment = realDeploymentGenerator.getDeployment();
        Service service = apolloTestClient.getService(deployment.getServiceId());
        apolloTestClient.updateService(service.getId(), service.getName(), service.getDeploymentYaml(), service.getServiceYaml(), true);

        apolloToKubernetes = apolloToKubernetesStore.getOrCreateApolloToKubernetes(deployment);

        assertImageName(apolloToKubernetes.getKubernetesDeployment(), "great image");
        assertDeploymentLabelExists(apolloToKubernetes.getKubernetesDeployment(), "such key", "much value");
    }


    private void assertImageName(io.fabric8.kubernetes.api.model.extensions.Deployment deployment, String imageName) {
        assertThat(deployment.getSpec().getTemplate().getSpec().getContainers().stream().findFirst().get().getImage()).contains(imageName);
    }

    private void assertDeploymentLabelExists(io.fabric8.kubernetes.api.model.extensions.Deployment deployment, String labelKey, String labelValue) {
        assertThat(deployment.getMetadata().getLabels().get(labelKey)).isEqualTo(labelValue);
    }
}
