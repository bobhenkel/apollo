package io.logz.apollo.auth;

import org.rapidoid.util.Tokens;

/**
 * Created by roiravhon on 5/23/17.
 */
public class TokenConverter {

    public static String convertTokenToUser(String token) {
        return Tokens.deserialize(token).get(Tokens._USER).toString();
    }
}
