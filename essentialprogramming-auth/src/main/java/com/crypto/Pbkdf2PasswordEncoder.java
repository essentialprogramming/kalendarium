package com.crypto;


import com.authentication.exceptions.codes.ErrorCode;
import com.util.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Locale;

public class Pbkdf2PasswordEncoder implements PasswordEncoder {

    final static Logger logger = LoggerFactory.getLogger(Pbkdf2PasswordEncoder.class);

    /**
     * Length of salt for PBKDF2 hashed password
     */
    private static final int PBKDF2_SALT_LENGTH = 16; // bytes
    private static final int PBKDF2_ITERATIONS = 200000;
    private static final int PBKDF2_HASH_BYTE_SIZE = 64;
    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA1"; // can also be PBKDF2WithHmacSHA256, PBKDF2WithHmacMD5 etc.

    private static class Pbkdf2PasswordEncoderHolder {
        static final Pbkdf2PasswordEncoder INSTANCE = new Pbkdf2PasswordEncoder();
    }

    public static Pbkdf2PasswordEncoder getInstance() {
        return Pbkdf2PasswordEncoderHolder.INSTANCE;
    }

    public String encode(CharSequence password) {
        logger.debug("Encoding password");
        String passwordLower = password.toString().toLowerCase(Locale.ENGLISH);
        try {
            return createHash(passwordLower.toCharArray());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error(ErrorCode.PASSWORD_HASH_CREATION_NOT_SUCCESFUL.getDescription(), e);
            throw new ServiceException(ErrorCode.PASSWORD_HASH_CREATION_NOT_SUCCESFUL);
        }
    }

    /**
     * Returns a salted hash of the password.
     *
     * @param password the password to hash
     * @return a salted hash of the password in the form of SALT:HASH
     * @throws InvalidKeySpecException, NoSuchAlgorithmException
     */
    private static String createHash(char[] password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        logger.debug("create hash");
        // Generate a random salt
        byte[] salt = EncodingUtils.getSalt(PBKDF2_SALT_LENGTH);

        // Hash the password
        byte[] hash = encodeWithSalt(salt, password);

        // Format hash:salt
        return EncodingUtils.toBase64(hash) + ":" + EncodingUtils.toBase64(salt)+ ":"+ "3";
    }

    private static byte[] encodeWithSalt(byte[] salt, char[] password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        logger.debug("Encode with salt");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, PBKDF2_ITERATIONS, PBKDF2_HASH_BYTE_SIZE * 8);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(HASH_ALGORITHM);

        return secretKeyFactory.generateSecret(keySpec).getEncoded();
    }


    /**
     * Validates a password using a hash.
     * Verify that the encoded password obtained from storage matches the submitted raw
     * password after it is too encoded. Returns true if the passwords match, false if
     * they do not. The stored password itself is never decoded.
     *
     * @param password               the password to check
     * @param hashedPasswordWithSalt the stored hashed password with salt
     * @return true if the password is correct, false if not
     */
    @Override
    public boolean matches(CharSequence password, String hashedPasswordWithSalt) {
        String passwordLower = password.toString().toLowerCase(Locale.ENGLISH);
        String[] parts = hashedPasswordWithSalt.split(":");
        if (parts.length < 2) {
            return false;
        }

        byte[] hash = EncodingUtils.fromBase64(parts[0]);
        byte[] salt = EncodingUtils.fromBase64(parts[1]);


        return matches(passwordLower, hash, salt);
    }

    /**
     * Validates a password using a hash.
     *
     * @param password the password to check
     * @param hash hashed password
     * @param salt Salt used to hash password
     * @return true if the password is correct, false if not
     */
    private static boolean matches(String password, byte[] hash, byte[] salt) {
        boolean result;
        try {
            result = matches(password.toCharArray(), hash, salt);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            logger.error(ErrorCode.PASSWORD_HASH_CREATION_NOT_SUCCESFUL.getDescription(), e);
            throw new ServiceException(ErrorCode.PASSWORD_HASH_CREATION_NOT_SUCCESFUL);
        }

        return result;
    }

    /**
     * Validates a password using a hash.
     *
     * @param password the password to check
     * @param hash hashed password
     * @param salt Salt used to hash password
     * @return true if the password is correct, false if not
     */
    private static boolean matches(char[] password, byte[] hash, byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException {

        // Compute the hash of the provided password, using the same salt, iteration count and hash length
        byte[] testHash = encodeWithSalt(salt, password);

        // Compare the hashes in constant time. The password is correct if both hashes match.
        return EncodingUtils.slowEquals(hash, testHash);

    }
}
