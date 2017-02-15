package io.logz.apollo.kubernetes;

import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by roiravhon on 2/2/17.
 */
public class ApolloToKubernetesFactory {

    private static ApolloToKubernetesFactory instance;
    private Map<Integer, ApolloToKubernetes> apolloToKubernetesMap;

    private ApolloToKubernetesFactory() {
        apolloToKubernetesMap = new HashMap<>();
    }

    private static synchronized ApolloToKubernetesFactory getInstance() {

        if (instance == null) {
            instance = new ApolloToKubernetesFactory();
        }

        return instance;
    }

    public static ApolloToKubernetes getOrCreateApolloToKubernetes(Deployment deployment) {

        ApolloToKubernetesFactory instance = getInstance();
        return instance.apolloToKubernetesMap.computeIfAbsent(deployment.getId(), key -> new ApolloToKubernetes(deployment));
    }
}
