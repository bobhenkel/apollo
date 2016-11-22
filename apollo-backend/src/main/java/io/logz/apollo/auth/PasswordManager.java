package io.logz.apollo.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by roiravhon on 11/21/16.
 */
public class PasswordManager {

    public static String encryptPassword(String password) {

        // Create MessageDigest instance for MD5
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());

            byte[] bytes = digest.digest();

            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            //Get complete hashed password in hex format
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not find algorithm MD5!", e);
        }
    }

    public static Boolean checkPassword(String plainPassword, String hashedPassword) {
        return encryptPassword(plainPassword).equals(hashedPassword);
    }
}
