package io.logz.apollo.clients;

import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.ModelsGenerator;

public class ApolloTestClient extends ApolloClient {

    public ApolloTestClient(String hostname, int port, String protocol) {
        super(ModelsGenerator.createRegularUser(), Common.DEFAULT_PASSWORD, hostname, port, protocol);
    }

}