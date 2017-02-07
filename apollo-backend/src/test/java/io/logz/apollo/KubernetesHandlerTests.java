package io.logz.apollo;


import io.fabric8.kubernetes.client.mock.KubernetesMockClient;
import io.logz.apollo.kubernetes.KubernetesHandler;

/**
 * Created by roiravhon on 2/7/17.
 */
public class KubernetesHandlerTests {

    KubernetesMockClient kubernetesMockClient;
    KubernetesHandler kubernetesHandler;

    public KubernetesHandlerTests() {

        kubernetesMockClient = new KubernetesMockClient();

        
    }
}
