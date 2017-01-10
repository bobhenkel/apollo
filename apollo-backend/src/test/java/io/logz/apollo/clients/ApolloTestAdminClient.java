package io.logz.apollo.clients;

import io.logz.apollo.auth.User;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.ModelsGenerator;

/**
 * Created by roiravhon on 11/28/16.
 */
public class ApolloTestAdminClient extends ApolloAdminClient {
    public ApolloTestAdminClient(ApolloConfiguration apolloConfiguration) {
        super(createAndRegisterAdminUserInDb(), Common.DEFAULT_PASSWORD, apolloConfiguration);
    }

    // Super must be the first call in the c'tor, so helper method here
    private static User createAndRegisterAdminUserInDb() {
        User user = ModelsGenerator.createAdminUser();
        Common.registerUserInDb(user);
        return user;
    }
}
