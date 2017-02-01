package io.logz.apollo.transformers.service;

import io.fabric8.kubernetes.api.model.Service;

/**
 * Created by roiravhon on 1/31/17.
 */
public interface BaseServiceTransformer {
    Service transform(Service service,
                      io.logz.apollo.models.Deployment apolloDeployment,
                      io.logz.apollo.models.Service apolloService,
                      io.logz.apollo.models.Environment apolloEnvironment,
                      io.logz.apollo.models.DeployableVersion apolloDeployableVersion);
}
