package com.crypto;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;


public class Argon2PasswordEncoder extends AbstractPasswordEncoder {

    final static Logger logger = LoggerFactory.getLogger(Argon2PasswordEncoder.class);

    private static final int ARGON2_HASH_ALGORITHM = 2;
    private static final int ARGON2_SALT_LENGTH = 16;
    private static final int ARGON2_HASH_LENGTH = 32;
    private static final int ARGON2_PARALLELISM = 1;
    private static final int ARGON2_MEMORY = 65536;
    private static final int ARGON2_ITERATIONS = 2;

    private static final class Argon2PasswordEncoderHolder {
        static final Argon2PasswordEncoder INSTANCE = new Argon2PasswordEncoder();
    }

    public static Argon2PasswordEncoder getInstance() {
        return Argon2PasswordEncoderHolder.INSTANCE;
    }

    public String encode(CharSequence password) {
        logger.debug("Encoding password");
        String passwordLower = password.toString();

        return createHash(passwordLower.toCharArray());
    }

    /**
     * Returns a salted hash of the password.
     *
     * @param password the password to hash
     * @return a salted hash of the password in the form of HASH:SALT:ALGORITHM
     */
    private static String createHash(char[] password) {
        logger.debug("create hash");
        // Generate a random salt
        String salt = EncodingUtils.getRandomString(ARGON2_SALT_LENGTH);

        // Hash the password
        byte[] hash = encodeWithSalt(salt.getBytes(), password);

        // Format hash:salt:algorithm
        return String.valueOf(Hex.encode(hash)) + ":" + salt + ":" + "2";
    }

    private static byte[] encodeWithSalt(byte[] salt, char[] password) {
        byte[] hash = new byte[ARGON2_HASH_LENGTH];
        Argon2Parameters params = (new Argon2Parameters.Builder(ARGON2_HASH_ALGORITHM))
                .withSalt(salt)
                .withParallelism(ARGON2_PARALLELISM)
                .withMemoryAsKB(ARGON2_MEMORY)
                .withIterations(ARGON2_ITERATIONS)
                .build();
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(params);
        generator.generateBytes(password, hash);
        return hash;
    }



    /**
     * Validates a password using a hash.
     *
     * @param password the password to check
     * @param hash hashed password
     * @param salt Salt used to hash password
     * @return true if the password is correct, false if not
     */
     boolean matches(String password, byte[] hash, byte[] salt) {
        return matches(password.toCharArray(), hash, salt);
    }

    /**
     * Validates a password using a hash.
     *
     * @param password the password to check
     * @param hash hashed password
     * @param salt Salt used to hash password
     * @return true if the password is correct, false if not
     */
    private static boolean matches(char[] password, byte[] hash, byte[] salt) {

        // Compute the hash of the provided password, using the same salt, iteration count and hash length
        byte[] testHash = encodeWithSalt(salt, password);
        // Compare the hashes in constant time. The password is correct if both hashes match.
        return EncodingUtils.slowEquals(hash, testHash);

    }

}
