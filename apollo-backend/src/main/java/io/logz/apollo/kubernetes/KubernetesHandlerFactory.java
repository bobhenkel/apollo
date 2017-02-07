package io.logz.apollo.kubernetes;

import com.google.common.annotations.VisibleForTesting;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.logz.apollo.models.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by roiravhon on 2/2/17.
 */
public class KubernetesHandlerFactory {

    private static KubernetesHandlerFactory instance;
    private Map<Integer, KubernetesHandler> kubernetesHandlerMap;

    private KubernetesHandlerFactory() {
        kubernetesHandlerMap = new HashMap<>();
    }

    private static KubernetesHandlerFactory getInstance() {

        if (instance == null) {
            instance = new KubernetesHandlerFactory();
        }

        return instance;
    }

    public static KubernetesHandler getOrCreateKubernetesHandler(Environment environment) {

        KubernetesHandlerFactory instance = getInstance();
        return instance.kubernetesHandlerMap.computeIfAbsent(environment.getId(), key -> new KubernetesHandler(environment));
    }

    @VisibleForTesting
    public static KubernetesHandler getOrCreateKubernetesHandlerWithSpecificClient(Environment environment, KubernetesClient kubernetesClient) {

        KubernetesHandlerFactory instance = getInstance();
        return instance.kubernetesHandlerMap.computeIfAbsent(environment.getId(), key -> new KubernetesHandler(environment, kubernetesClient));
    }
}
