package io.logz.apollo.clients;

import io.logz.apollo.auth.User;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.ModelsGenerator;

/**
 * Created by roiravhon on 11/28/16.
 */
public class ApolloTestAdminClient extends ApolloAdminClient {
    public ApolloTestAdminClient(ApolloConfiguration apolloConfiguration, UserDao userDao) {
        super(createAndRegisterAdminUserInDb(userDao), Common.DEFAULT_PASSWORD, apolloConfiguration);
    }

    // Super must be the first call in the c'tor, so helper method here
    private static User createAndRegisterAdminUserInDb(UserDao userDao) {
        User user = ModelsGenerator.createAdminUser();
        userDao.addUser(user);
        return user;
    }
}
