package io.logz.apollo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.fabric8.kubernetes.api.model.KubernetesList;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by roiravhon on 1/15/17.
 */
public class TestGround {

    @Test
    public void testGround() throws IOException {

        Config config = new ConfigBuilder().withMasterUrl("https://172.31.24.6:6443").withOauthToken("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImFwb2xsby10b2tlbi1pZHZ4OCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJhcG9sbG8iLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI5MDAwYjA1ZS1hY2MwLTExZTYtOWI1ZC0wYTc3MGIxYWQ4NDAiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6ZGVmYXVsdDphcG9sbG8ifQ.HNzku2KCbqClA_APVWsS_r4NxAS_Kf9-2eW770PJTT7buoAdJNLMjgObAyc4-Lh0D6BRzkcY8JmCsILcnisgy5OWmoY86qV1011mYIb-_SINa11O8TpEraqYbLZGk2GbQCJaXUzlyzvaXx2eJoZ1XABhDWLp7D4JdM0-ac4kY9_vKcVGnosrB8-zIMqWZVjd467wKyv78-kG3uQr2mkP4ZKY-JKAxkcK_WOC-AgsRfN9ekPfeSCFj3gqrRB5_ep9bLPURf8sEcdb3LYwb81QH0S5-2ZqNM170gWsGv5tUvBXXhox0J9FQm1u9J9EyD3tPiDBB6x7OyyyoT7x64bDyA").build();
        KubernetesClient kubernetesClient = new DefaultKubernetesClient(config);

        String json = "{\n" +
                "    \"kind\": \"Deployment\",\n" +
                "    \"apiVersion\": \"extensions/v1beta1\",\n" +
                "    \"metadata\": {\n" +
                "        \"name\": \"nginx\",\n" +
                "        \"namespace\": \"default\",\n" +
                "        \"labels\": {\n" +
                "            \"app\": \"nginx\"\n" +
                "        }  \n" +
                "    },\n" +
                "    \"spec\": {\n" +
                "        \"replicas\": 1,\n" +
                "        \"selector\": {\n" +
                "            \"matchLabels\": {\n" +
                "                \"app\": \"nginx\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"template\": {\n" +
                "            \"metadata\": {\n" +
                "                \"labels\": {\n" +
                "                    \"app\": \"nginx\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"spec\": {\n" +
                "                \"containers\": [\n" +
                "                    {\n" +
                "                        \"name\": \"roi-apollo-test\",\n" +
                "                        \"image\": \"registry.internal.logz.io:5000/roi-sample-app\",\n" +
                "                        \"ports\": [\n" +
                "                            {\n" +
                "                                \"containerPort\": 80,\n" +
                "                                \"protocol\": \"TCP\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"resources\": {},\n" +
                "                        \"imagePullPolicy\": \"Always\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"restartPolicy\": \"Always\",\n" +
                "                \"terminationGracePeriodSeconds\": 30,\n" +
                "                \"dnsPolicy\": \"ClusterFirst\",\n" +
                "                \"securityContext\": {}\n" +
                "            }\n" +
                "        },\n" +
                "        \"strategy\": {\n" +
                "            \"type\": \"RollingUpdate\",\n" +
                "            \"rollingUpdate\": {\n" +
                "                \"maxUnavailable\": 0,\n" +
                "                \"maxSurge\": 1\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";


        String yaml = "apiVersion: extensions/v1beta1\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  labels:\n" +
                "    app: nginx\n" +
                "  name: nginx\n" +
                "  namespace: default\n" +
                "spec:\n" +
                "  replicas: 1\n" +
                "  selector:\n" +
                "    matchLabels:\n" +
                "      app: nginx\n" +
                "  strategy:\n" +
                "    rollingUpdate:\n" +
                "      maxSurge: 1\n" +
                "      maxUnavailable: 0\n" +
                "    type: RollingUpdate\n" +
                "  template:\n" +
                "    metadata:\n" +
                "      labels:\n" +
                "        app: nginx\n" +
                "    spec:\n" +
                "      containers:\n" +
                "      - image: registry.internal.logz.io:5000/roi-sample-app\n" +
                "        imagePullPolicy: Always\n" +
                "        name: roi-apollo-test\n" +
                "        ports:\n" +
                "        - containerPort: 80\n" +
                "          protocol: TCP\n" +
                "        resources: {}\n" +
                "      dnsPolicy: ClusterFirst\n" +
                "      restartPolicy: Always\n" +
                "      securityContext: {}\n" +
                "      terminationGracePeriodSeconds: 30";

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Deployment deployment = mapper.readValue(yaml, Deployment.class);

        deployment.getSpec().getTemplate().getSpec().getContainers().forEach(container -> container.setImage(container.getImage() + ":1"));
        kubernetesClient.extensions().deployments().inNamespace("default").createOrReplace(deployment);

        System.out.println(deployment.getSpec().getTemplate().getSpec().getContainers().stream().findFirst().get().getImage());

//        Deployment deployment = new DeploymentBuilder()
//                .withNewMetadata()
//                .withName("nginx")
//                .endMetadata()
//                .withNewSpec()
//                .withReplicas(1)
//                .withNewTemplate()
//                .withNewMetadata()
//                .addToLabels("app", "nginx")
//                .endMetadata()
//                .withNewSpec()
//                .addNewContainer()
//                .withName("roi-apollo-test")
//                .withImage("registry.internal.logz.io:5000/roi-sample-app:2")
//                .withImagePullPolicy("Always")
//                .addNewPort()
//                .withContainerPort(80)
//                .endPort()
//                .endContainer()
//                .endSpec()
//                .endTemplate()
//                .endSpec()
//                .build();
//
//        Service service = new ServiceBuilder()
//                .withNewMetadata()
//                .withName("roi-test-service")
//                .addToLabels("app", "nginx")
//                .endMetadata()
//                .withNewSpec()
//                .withType("NodePort")
//                .addNewPort()
//                .withPort(80)
//                .withNodePort(30002)
//                .withProtocol("TCP")
//                .endPort()
//                .addToSelector("app", "nginx")
//                .endSpec()
//                .build();
//
//        //Namespace namespace = new NamespaceBuilder().withNewMetadata().withName("default").endMetadata().build();
//        //ServiceAccount serviceAccount = new ServiceAccountBuilder().withNewMetadata().withName("apollo").endMetadata().build();
//
//        //kubernetesClient.namespaces().createOrReplace(namespace);
//        //kubernetesClient.serviceAccounts().inNamespace("default").createOrReplace(serviceAccount);
//        kubernetesClient.extensions().deployments().inNamespace("default").createOrReplace(deployment);
//        kubernetesClient.services().inNamespace("default").createOrReplace(service);
//
//        //DeploymentSpec n = new DeploymentSpecBuilder().with
//        //Deployment deployment = new DeploymentBuilder().with
//
//        //kubernetesClient.extensions().deployments().create()



    }
}
