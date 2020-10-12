package com.crypto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Pbkdf2PasswordEncoderTest {

    private Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = Pbkdf2PasswordEncoder.getInstance();

    @Test
    void password_should_be_equals_after_encoding() {
        String password = "Test123";
        String encodedPassword = pbkdf2PasswordEncoder.encode(password);

        Assertions.assertTrue(pbkdf2PasswordEncoder.matches(password, encodedPassword));
    }

    @Test
    void password_should_be_different_after_encoding() {
        String password = "Test123";
        String encodedPassword = pbkdf2PasswordEncoder.encode(password);

        Assertions.assertFalse(PasswordHash.matches("Test456", encodedPassword));
    }
}
