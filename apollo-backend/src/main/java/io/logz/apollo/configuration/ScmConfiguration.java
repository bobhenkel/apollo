package io.logz.apollo.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ScmConfiguration {

    private String githubLogin;
    private String githubOauthToken;

    @JsonCreator
    public ScmConfiguration(@JsonProperty("githubLogin") String githubLogin,
                            @JsonProperty("githubOauthToken") String githubOauthToken) {
        this.githubLogin = githubLogin;
        this.githubOauthToken = githubOauthToken;
    }

    public String getGithubLogin() {
        return githubLogin;
    }

    public String getGithubOauthToken() {
        return githubOauthToken;
    }

}
