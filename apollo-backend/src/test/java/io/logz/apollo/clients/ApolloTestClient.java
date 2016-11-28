package io.logz.apollo.clients;

import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.helpers.Common;


/**
 * Created by roiravhon on 11/28/16.
 */
public class ApolloTestClient extends ApolloClient {

    public ApolloTestClient(ApolloConfiguration apolloConfiguration) {
        super(Common.createRegularUser(), Common.DEFAULT_PASSWORD, apolloConfiguration);
    }
}