package io.logz.apollo.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KubernetesConfiguration {

    private int monitoringFrequencySeconds;

    @JsonCreator
    public KubernetesConfiguration(@JsonProperty("monitoringFrequencySeconds") int monitoringFrequencySeconds) {
        this.monitoringFrequencySeconds = monitoringFrequencySeconds;
    }

    public int getMonitoringFrequencySeconds() {
        return monitoringFrequencySeconds;
    }

}
