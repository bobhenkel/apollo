package io.logz.apollo.auth;

import io.logz.apollo.common.Encryptor;

/**
 * Created by roiravhon on 11/21/16.
 */
public class PasswordManager {

    public static String encryptPassword(String password) {
        return Encryptor.encryptString(password);
    }

    public static Boolean checkPassword(String plainPassword, String hashedPassword) {
        return encryptPassword(plainPassword).equals(hashedPassword);
    }
}
