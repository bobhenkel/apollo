package io.logz.apollo.helpers;

import com.google.gson.Gson;
import io.logz.apollo.auth.PasswordManager;
import io.logz.apollo.auth.User;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.database.ApolloMyBatis;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by roiravhon on 11/23/16.
 */
public class Common {

    public static final String DEFAULT_PASSWORD = "123456";

    public static String randomStr(int size) {
        return UUID.randomUUID().toString().substring(0, size);
    }



    public static int getAvailablePort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        int port = socket.getLocalPort();
        socket.close();

        return port;
    }

    public static void registerUserInDb(User user) {
        ApolloMyBatis.getDao(UserDao.class).addUser(user);
    }

    public static String generateJson(String... keyValuePairs ) {

        Map<String, String> jsonMap = new HashMap<>();

        String latestKey = null;
        for (String currString : keyValuePairs) {

            if (latestKey == null) {
                latestKey = currString;
                continue;
            }

            jsonMap.put(latestKey, currString);
            latestKey = null;
        }

        if (latestKey != null) {
            throw new RuntimeException("Got un-even number of arguments!");
        }

        Gson gson = new Gson();
        return gson.toJson(jsonMap);
    }

    public static User createRegularUser() {
        return createUser(false);
    }

    public static User createAdminUser() {
        return createUser(true);
    }

    private static User createUser(boolean admin) {

        User testUser = new User();
        testUser.setUserEmail("tahat+" + randomStr(5) + "@logz.io");
        testUser.setFirstName("Tahat " + randomStr(5));
        testUser.setLastName("Tahatson " + randomStr(5));
        testUser.setHashedPassword(PasswordManager.encryptPassword(DEFAULT_PASSWORD));
        testUser.setAdmin(admin);

        return testUser;
    }
}
