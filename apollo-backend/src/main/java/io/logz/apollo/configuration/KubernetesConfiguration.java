package io.logz.apollo.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KubernetesConfiguration {

    private int monitoringFrequencySeconds;
    private int healthFrequencySeconds;

    @JsonCreator
    public KubernetesConfiguration(@JsonProperty("monitoringFrequencySeconds") int monitoringFrequencySeconds,
                                   @JsonProperty("healthFrequencySeconds") int healthFrequencySeconds) {
        this.monitoringFrequencySeconds = monitoringFrequencySeconds;
        this.healthFrequencySeconds = healthFrequencySeconds;
    }

    public int getMonitoringFrequencySeconds() {
        return monitoringFrequencySeconds;
    }

    public int getHealthFrequencySeconds() {
        return healthFrequencySeconds;
    }

}
