package io.logz.apollo.clients;

import io.logz.apollo.models.User;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.ModelsGenerator;

/**
 * Created by roiravhon on 11/28/16.
 */
public class ApolloTestAdminClient extends ApolloAdminClient {
    public ApolloTestAdminClient(String hostname, int port, String protocol, UserDao userDao) {
        super(createAndRegisterAdminUserInDb(userDao), Common.DEFAULT_PASSWORD, hostname, port, protocol);
    }

    // Super must be the first call in the c'tor, so helper method here
    private static User createAndRegisterAdminUserInDb(UserDao userDao) {
        User user = ModelsGenerator.createAdminUser();
        userDao.addUser(user);
        return user;
    }
}
