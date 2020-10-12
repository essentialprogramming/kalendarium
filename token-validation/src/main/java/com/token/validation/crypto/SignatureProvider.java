package com.token.validation.crypto;

import com.token.validation.jwt.Jwt;
import com.token.validation.jwt.exception.TokenValidationException;

import java.security.Key;

public interface SignatureProvider {

    boolean verify(Jwt input, Key key) throws TokenValidationException;
}
