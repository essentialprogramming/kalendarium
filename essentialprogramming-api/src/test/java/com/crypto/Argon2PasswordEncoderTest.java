package com.crypto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Argon2PasswordEncoderTest {

    private Argon2PasswordEncoder argon2PasswordEncoder = Argon2PasswordEncoder.getInstance();

    @Test
    void password_should_be_equals_after_encoding() {
        String password = "Test123";
        String encodedPassword = argon2PasswordEncoder.encode(password);

        Assertions.assertTrue(argon2PasswordEncoder.matches(password, encodedPassword));
    }

    @Test
    void password_should_be_different_after_encoding() {
        String password = "Test123";
        String encodedPassword = argon2PasswordEncoder.encode(password);

        Assertions.assertFalse(argon2PasswordEncoder.matches("Test456", encodedPassword));
    }

}
