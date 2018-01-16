package io.logz.apollo.configuration;

import org.apache.commons.lang3.StringUtils;
import org.conf4j.consul.source.ConsulFileConfigurationSource;
import org.conf4j.core.ConfigurationProvider;
import org.conf4j.core.ConfigurationProviderBuilder;
import org.conf4j.core.source.ClasspathConfigurationSource;
import org.conf4j.core.source.ConfigurationSource;
import org.conf4j.core.source.FilesystemConfigurationSource;

public class ApolloConfigurationProviderBuilder {

    private static final String ROOT_PATH = "apollo";
    private static final String LOCAL_CONFIG_RESOURCE = "application.conf";

    public static ConfigurationProvider<ApolloConfiguration> build() {
        String localConfigFileName = getParam("APOLLO_CONFIG_FILEPATH", "");
        String consulUrl = getParam("APOLLO_CONSUL_URL");
        String consulKey = getParam("APOLLO_CONSUL_KEY");

        ClasspathConfigurationSource resourceConfigSource = ClasspathConfigurationSource.builder()
                .withResourcePath(LOCAL_CONFIG_RESOURCE)
                .build();

        if (StringUtils.isBlank(consulUrl) || StringUtils.isBlank(consulKey)) {
            return new ConfigurationProviderBuilder<>(ApolloConfiguration.class)
                    .withConfigurationSource(getFilesystemConfigurationSource(localConfigFileName, false))
                    .withConfigRootPath(ROOT_PATH)
                    .build();
        }

        return new ConfigurationProviderBuilder<>(ApolloConfiguration.class)
                .withConfigurationSource(createConsulSource(consulUrl, consulKey))
                .addFallback(getFilesystemConfigurationSource(localConfigFileName, true))
                .addFallback(resourceConfigSource)
                .withConfigRootPath(ROOT_PATH)
                .build();
    }

    private static FilesystemConfigurationSource getFilesystemConfigurationSource(String filePath, boolean ignoreIfMissing) {
        if (StringUtils.isBlank(filePath) && ignoreIfMissing) {
            return FilesystemConfigurationSource.builder()
                    .withFilePath(filePath)
                    .ignoreMissingFile()
                    .build();
        }

        return FilesystemConfigurationSource.builder()
                .withFilePath(filePath)
                .build();
    }

    private static ConfigurationSource createConsulSource(String consulUrl, String consulKey) {
        ConfigurationSource consulConfigurationSource = ConsulFileConfigurationSource.builder()
                .withConfigurationFilePath(consulKey.toLowerCase())
                .withConsulUrl(consulUrl)
                .reloadOnChange()
                .build();

        return consulConfigurationSource;
    }

    private static String getParam(String name, String defaultValue) {
        String value = System.getenv(name);
        if (StringUtils.isBlank(value)) {
            value = System.getProperty(name);
        }

        return StringUtils.isNotBlank(value) ? value : defaultValue;
    }

    private static String getParam(String name) {
        return getParam(name, null);
    }

}
