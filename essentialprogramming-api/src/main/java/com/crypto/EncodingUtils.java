package com.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Base64;

class EncodingUtils {

    private static final Logger logger = LoggerFactory.getLogger(EncodingUtils.class);

    static byte[] getSalt(int length) {
        logger.debug("Generate salt");
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return salt;
    }

    static String getRandomString(int length) {
        logger.debug("Generate salt");
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return EncodingUtils.toBase64(salt);
    }

    /**
     * Converts a byte array into a base64 string.
     *
     * @param array the byte array to convert
     * @return a length*2 character string encoding the byte array
     */
    static String toBase64(byte[] array) {
        logger.debug("Convert to base64");
        return Base64.getEncoder().encodeToString(array);
    }

    /**
     * Converts a string of base64 characters into a byte array.
     *
     * @param base64 the hex string
     * @return the hex string decoded into a byte array
     */
    static byte[] fromBase64(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    /**
     * Compares two byte arrays in length-constant time. This comparison method
     * is used so that password hashes cannot be extracted from an on-line
     * system using a timing attack and then attacked off-line.
     *
     * @param a the first byte array
     * @param b the second byte array
     * @return true if both byte arrays are the same, false if not
     */
    static boolean slowEquals(byte[] a, byte[] b) {

        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}
