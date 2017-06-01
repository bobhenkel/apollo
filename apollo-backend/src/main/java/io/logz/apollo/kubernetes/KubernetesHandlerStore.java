package io.logz.apollo.kubernetes;

import com.google.common.annotations.VisibleForTesting;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.logz.apollo.models.Environment;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 2/2/17.
 */
@Singleton
public class KubernetesHandlerStore {

    private final ApolloToKubernetesStore apolloToKubernetesStore;
    private final Map<Integer, KubernetesHandler> kubernetesHandlerMap;

    @Inject
    public KubernetesHandlerStore(ApolloToKubernetesStore apolloToKubernetesStore) {
        this.apolloToKubernetesStore = requireNonNull(apolloToKubernetesStore);
        this.kubernetesHandlerMap = new ConcurrentHashMap<>();
    }

    public KubernetesHandler getOrCreateKubernetesHandler(Environment environment) {
        return kubernetesHandlerMap.computeIfAbsent(environment.getId(),
                key -> new KubernetesHandler(apolloToKubernetesStore, environment));
    }

    @VisibleForTesting
    public KubernetesHandler getOrCreateKubernetesHandlerWithSpecificClient(Environment environment, KubernetesClient kubernetesClient) {
        return kubernetesHandlerMap.computeIfAbsent(environment.getId(),
                key -> new KubernetesHandler(apolloToKubernetesStore, kubernetesClient, environment));
    }

}
