package io.logz.apollo.helpers;

import io.logz.apollo.auth.PasswordManager;
import io.logz.apollo.auth.User;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.database.ApolloMyBatis;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.UUID;

/**
 * Created by roiravhon on 11/23/16.
 */
public class Common {

    public static final String DEFAULT_PASSWORD = "123456";

    public static String randomStr(int size) {
        return UUID.randomUUID().toString().substring(0, size);
    }

    public static User createUser(boolean admin) {

        User testUser = new User();
        testUser.setEmailAddress("tahat+" + randomStr(5) + "@logz.io");
        testUser.setFirstName("Tahat " + randomStr(5));
        testUser.setLastName("Tahatson " + randomStr(5));
        testUser.setHashedPassword(PasswordManager.encryptPassword(DEFAULT_PASSWORD));
        testUser.setAdmin(admin);

        return testUser;
    }

    public static int getAvilablePort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        int port = socket.getLocalPort();
        socket.close();

        return port;
    }

    public static void registerUserInDb(User user) {
        ApolloMyBatis.getDao(UserDao.class).addUser(user);
    }
}
