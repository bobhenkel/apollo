package io.logz.apollo.helpers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.logz.apollo.ApolloServer;
import io.logz.apollo.auth.User;
import io.logz.apollo.configuration.ApolloConfiguration;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static io.logz.apollo.helpers.Common.createUser;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by roiravhon on 11/23/16.
 */
public class RestClientHelper {

    private static final Logger logger = LoggerFactory.getLogger(RestClientHelper.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final ApolloServer server;
    private final OkHttpClient client;
    private String adminToken;
    private String baseUrl;

    // Helper class, because OkHttp response object cant be passed around due to its buffer
    public class RestResponse {

        private final int code;
        private final String body;

        public RestResponse(int code, String body) {
            this.code = code;
            this.body = body;
        }

        public int getCode() {
            return code;
        }

        public String getBody() {
            return body;
        }
    }

    public RestClientHelper(ApolloConfiguration apolloConfiguration) throws IOException {

        logger.info("Starting RestClient Helper class");

        // Set free port for the api to listen to
        apolloConfiguration.setApiPort(Common.getAvilablePort());

        // Start REST Server
        server = new ApolloServer(apolloConfiguration);
        server.start();

        client = new OkHttpClient();

        // Set the base URL
        baseUrl = "localhost:" + apolloConfiguration.getApiPort();

        logger.info("Base url for the API is: {}", baseUrl);
    }

    public RestResponse get(String url, String token) throws IOException {
        Request request = new Request.Builder().url(getFullUrlWithToken(url, token)).build();
        Response response = client.newCall(request).execute();
        return new RestResponse(response.code(), response.body().string());
    }

    public RestResponse post(String url, String json, String token) throws IOException {
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(getFullUrlWithToken(url, token)).post(requestBody).build();
        Response response = client.newCall(request).execute();
        return new RestResponse(response.code(), response.body().string());
    }

    public RestResponse adminGet(String url) throws IOException {
        return get(url, getAdminToken());
    }

    public RestResponse adminPost(String url, String json) throws IOException {
        return post(url, json, getAdminToken());
    }

    public String generateSignupJson(User user) {

        return "{" +
                "\"firstName\": \"" + user.getFirstName() + "\"," +
                "\"lastName\": \"" + user.getLastName() + "\"," +
                "\"emailAddress\": \"" + user.getEmailAddress() + "\"," +
                "\"password\": \"" + Common.DEFAULT_PASSWORD + "\"" +
                "}";
    }

    public String generateLoginJson(User user) {

        return "{" +
                "\"username\": \"" + user.getEmailAddress() + "\"," +
                "\"password\": \"" + Common.DEFAULT_PASSWORD + "\"" +
                "}";
    }

    public String getTokenFromResponse(RestResponse restResponse) throws IOException {

        JsonObject jsonObject = new JsonParser().parse(restResponse.getBody()).getAsJsonObject();
        return jsonObject.get("token").getAsString();
    }

    public RestResponse signup(User user) throws IOException {
        return adminPost("/signup", generateSignupJson(user));
    }

    public RestResponse login(User user) throws IOException {
        return post("/_login", generateLoginJson(user), "");
    }

    public void assertSuccessfulLogin(RestResponse restResponse) throws IOException {
        JsonObject jsonObject = new JsonParser().parse(restResponse.getBody()).getAsJsonObject();
        assertThat(jsonObject.get("success").getAsBoolean()).isTrue();
    }

    public void assertFailedLogin(RestResponse restResponse) throws IOException {
        JsonObject jsonObject = new JsonParser().parse(restResponse.getBody()).getAsJsonObject();
        assertThat(jsonObject.get("success").getAsBoolean()).isFalse();
    }

    private String getFullUrlWithToken(String url, String token) {

        // If the token is empty the url formation is broken
        if (token.equals("")) {
            token = "notavalidtoeknbutnotempty";
        }
        return "http://" + baseUrl + url + "?_token=" + token;
    }

    private String getAdminToken() throws IOException {

        if (adminToken == null) {
            User adminUser = createUser(true);
            Common.registerUserInDb(adminUser);
            RestResponse response = login(adminUser);

            assertSuccessfulLogin(response);
            adminToken = getTokenFromResponse(response);
        }

        return adminToken;
    }
}
