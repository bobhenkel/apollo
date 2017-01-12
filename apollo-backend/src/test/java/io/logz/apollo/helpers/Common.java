package io.logz.apollo.helpers;

import com.google.gson.Gson;
import io.logz.apollo.auth.User;
import io.logz.apollo.clients.ApolloTestAdminClient;
import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.exceptions.ApolloCouldNotLoginException;

import javax.script.ScriptException;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
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

    public static ApolloTestClient signupAndLogin() {

        try {
            // Create and login admin
            ApolloTestAdminClient apolloTestAdminClient = getAndLoginApolloTestAdminClient();

            // Create and signup user
            ApolloTestClient apolloTestClient = StandaloneApollo.getOrCreateServer().createTestClient();
            apolloTestAdminClient.signup(apolloTestClient.getClientUser(), Common.DEFAULT_PASSWORD);

            // Login the new user
            apolloTestClient.login();

            return apolloTestClient;

        } catch (Exception e) {
            throw new RuntimeException("Could not signup or login..", e);
        }
    }

    public static ApolloTestAdminClient getAndLoginApolloTestAdminClient() throws ScriptException, IOException, SQLException, ApolloCouldNotLoginException {
        ApolloTestAdminClient apolloTestAdminClient = StandaloneApollo.getOrCreateServer().createTestAdminClient();
        apolloTestAdminClient.login();
        return apolloTestAdminClient;
    }
}
