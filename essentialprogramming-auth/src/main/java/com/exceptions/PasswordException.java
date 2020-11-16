package com.exceptions;


import com.util.password.PasswordStrength;

public class PasswordException extends Exception {

    private static final long serialVersionUID = 1204672711345703311L;
    private final PasswordStrength passwordStrength;

    public PasswordException(String message, PasswordStrength passwordStrength) {
        super(message);
        this.passwordStrength = passwordStrength;
    }

    public PasswordStrength getPasswordStrength() {
        return passwordStrength;
    }
}
