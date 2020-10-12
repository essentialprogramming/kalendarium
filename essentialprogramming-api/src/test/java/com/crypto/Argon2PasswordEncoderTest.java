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

    @Test
    void test() {
        String password = "Test1234";
        Assertions.assertTrue(PasswordHash.matches(password, "dff5f440a42de85a95da14cca9d7bfc63dc0abebc8c2635ebf1fd94c098cec0d:32VsTU2zGkve1VLW:2"));
    }
}
