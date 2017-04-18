package io.logz.apollo.transformers.service;

import io.fabric8.kubernetes.api.model.Service;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;

/**
 * Created by roiravhon on 3/28/17.
 */
public class ServiceNodePortCoefficientTransformer implements BaseServiceTransformer {
    @Override
    public Service transform(Service service, Deployment apolloDeployment, io.logz.apollo.models.Service apolloService, Environment apolloEnvironment, DeployableVersion apolloDeployableVersion) {

        service.getSpec().getPorts().forEach(servicePort -> {
            if (servicePort.getNodePort() != null) {
                servicePort.setNodePort(servicePort.getNodePort() + apolloEnvironment.getServicePortCoefficient());
            }
        });

        return service;
    }
}
