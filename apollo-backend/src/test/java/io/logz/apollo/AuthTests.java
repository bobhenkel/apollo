package io.logz.apollo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.logz.apollo.auth.User;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.helpers.ApolloHelper;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.RestClientHelper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by roiravhon on 11/22/16.
 */
public class AuthTests {

    private static final Logger logger = LoggerFactory.getLogger(AuthTests.class);
    private final ApolloHelper apolloHelper;

    public AuthTests() throws ScriptException, IOException, SQLException {

        apolloHelper = ApolloHelper.getInstance();
    }

    @Test
    public void testSignup() throws IOException {

        // Create a regular user
        User regularUser = Common.createUser(false);

        // One response object that we will use
        RestClientHelper.RestResponse response;

        // Sign it up
        response = apolloHelper.rest().signup(regularUser);
        assertThat(response.getCode()).isEqualTo(200);

        // Lets signup again, we should fail
        response = apolloHelper.rest().signup(regularUser);
        assertThat(response.getCode()).isEqualTo(500);

        // And try to signup without a token, should result in forbidden
        response = apolloHelper.rest().post("/signup", apolloHelper.rest().generateSignupJson(regularUser), "");
        assertThat(response.getCode()).isEqualTo(403);
    }

    @Test
    public void testLogin() throws IOException {

        // Create a regular user
        User regularUser = Common.createUser(false);

        // One response object that we will use
        RestClientHelper.RestResponse response;

        // Sign it up
        response = apolloHelper.rest().signup(regularUser);
        assertThat(response.getCode()).isEqualTo(200);

        // Login
        response = apolloHelper.rest().login(regularUser);
        apolloHelper.rest().assertSuccessfulLogin(response);

        // Create a user without signup
        User nonExistentUser = Common.createUser(false);
        response = apolloHelper.rest().login(nonExistentUser);
        apolloHelper.rest().assertFailedLogin(response);
    }

    @Test
    public void testGetAllUsers() throws IOException {

        // Create a regular user
        User regularUser = Common.createUser(false);

        // Signing it up
        RestClientHelper.RestResponse response;
        response = apolloHelper.rest().signup(regularUser);
        assertThat(response.getCode()).isEqualTo(200);

        // Login and obtain token
        String token = apolloHelper.rest().getTokenFromResponse(apolloHelper.rest().login(regularUser));

        // Checking the number of users in DB
        int numberOfUSersInDb = ApolloMyBatis.getDao(UserDao.class).getAllUsers().size();

        // Get all users
        response = apolloHelper.rest().get("/users", token);

        JsonArray jsonArray = new JsonParser().parse(response.getBody()).getAsJsonArray();
        assertThat(jsonArray.size()).isEqualTo(numberOfUSersInDb);

        CountDownLatch currentUserFound = new CountDownLatch(1);
        jsonArray.forEach(user -> {
            JsonObject currUser = user.getAsJsonObject();
            if (currUser.get("emailAddress").getAsString().equals(regularUser.getEmailAddress())) {

                assertThat(currUser.get("firstName").getAsString().equals(regularUser.getFirstName())).isTrue();
                assertThat(currUser.get("lastName").getAsString().equals(regularUser.getLastName())).isTrue();
                assertThat(currUser.get("admin").getAsBoolean()).isEqualTo(regularUser.isAdmin());
                assertThat(currUser.get("hashedPassword").getAsString()).contains("*");
                currentUserFound.countDown();
            }
        });

        // Check that we found our user
        assertThat(currentUserFound.getCount()).isEqualTo(0);
    }
}
