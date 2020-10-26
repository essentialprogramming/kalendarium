package com.crypto;

import org.springframework.security.crypto.codec.Hex;

public abstract class AbstractPasswordEncoder implements PasswordEncoder {


    /**
     * Validates a password using a hash.
     * Verify that the encoded password obtained from storage matches the submitted raw
     * password after it is too encoded. Returns true if the passwords match, false if
     * they do not. The stored password itself is never decoded.
     *
     * @param password               the password to check
     * @param encodedPassword the stored hashed password with salt
     * @return true if the password is correct, false if not
     */
    @Override
    public boolean matches(CharSequence password, String encodedPassword) {
        String passwordLower = password.toString();
        String[] parts = encodedPassword.split(":");
        if (parts.length < 2) {
            return false;
        }
        byte[] hash = Hex.decode(parts[0]);
        byte[] salt = parts[1].getBytes();


        return matches(passwordLower, hash, salt);
    }

    abstract boolean matches(String password, byte[] hash, byte[] salt);
}
