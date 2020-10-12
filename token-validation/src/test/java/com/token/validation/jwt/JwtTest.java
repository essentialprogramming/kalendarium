package com.token.validation.jwt;

import com.token.validation.jwt.exception.TokenValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTest {

    private KeyPair keyPair;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        keyPair = generateRSAKeyPair();
    }

    @Test
    void testJwt() throws SignatureException, InvalidKeyException, NoSuchAlgorithmException,
            TokenValidationException {
        // create header
        String header = "{ \"alg\" : \"RS256\", \"typ\" : \"JWT\" }";
        String encodedHeader = Base64.getUrlEncoder().encodeToString(header.getBytes());

        // create payload
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        String payload = "{ \"iss\" : \"issuer\", \"iat\" : \"" + now.getTime() / 1000 + "\" }";
        String encodedPayload = Base64.getUrlEncoder().encodeToString(payload.getBytes());

        // create jwt
        StringBuilder jwt = new StringBuilder();
        jwt.append(encodedHeader);
        jwt.append(".");
        jwt.append(encodedPayload);

        // sign jwt
        PrivateKey key = keyPair.getPrivate();

        byte[] sign = signWithRSA256(jwt.toString().getBytes(), key);
        String encodedSignature = Base64.getUrlEncoder().encodeToString(sign);

        jwt.append(".");
        jwt.append(encodedSignature);

        Jwt jwtToken = new Jwt(jwt.toString());

        Assertions.assertEquals(encodedHeader, jwtToken.getEncodedHeader());
        Assertions.assertEquals(encodedPayload, jwtToken.getEncodedContent());
        Assertions.assertEquals(encodedSignature, jwtToken.getEncodedSignature());
        Assertions.assertArrayEquals(sign, jwtToken.getSignature());
        Assertions.assertArrayEquals(payload.getBytes(), jwtToken.getContent());
    }

    @Test
    void testJwtNoTSigned() throws TokenValidationException {

        String header = "{ \"alg\" : \"RS256\", \"typ\" : \"JWT\" }";
        String encodedHeader = Base64.getUrlEncoder().encodeToString(header.getBytes());

        // create payload
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        String payload = "{ \"iss\" : \"issuer\", \"iat\" : \"" + now.getTime() / 1000 + "\" }";
        String encodedPayload = Base64.getUrlEncoder().encodeToString(payload.getBytes());

        // create jwt
        String jwt = encodedHeader +
                "." +
                encodedPayload;
        Jwt jwtToken = new Jwt(jwt);
    }

    @Test
    void testInvalidJwtStructure() throws TokenValidationException {

        String header = "{ \"alg\" : \"RS256\", \"typ\" : \"JWT\" }";
        String encodedHeader = Base64.getUrlEncoder().encodeToString(header.getBytes());

        // create jwt
        StringBuilder jwt = new StringBuilder();
        jwt.append(encodedHeader);

        Exception exception = assertThrows(TokenValidationException.class, () -> new Jwt(jwt.toString()));
        Assertions.assertTrue(exception.getMessage().contains("Parsing error"));
    }

    @Test
    void TestInvalidJwtStructure() throws TokenValidationException {

        String jwtPart1 = "{ \"alg\" : \"RS256\", \"typ\" : \"JWT\" }";
        String encodedJwtPart1 = Base64.getUrlEncoder().encodeToString(jwtPart1.getBytes());

        String jwtPart2 = "{ \"alg\" : \"RS256\", \"typ\" : \"JWT\" }";
        String encodedJwtPart2 = Base64.getUrlEncoder().encodeToString(jwtPart2.getBytes());

        String jwtPart3 = "{ \"alg\" : \"RS256\", \"typ\" : \"JWT\" }";
        String encodedJwtPart3 = Base64.getUrlEncoder().encodeToString(jwtPart3.getBytes());

        String jwtPart4 = "{ \"alg\" : \"RS256\", \"typ\" : \"JWT\" }";
        String encodedJwtPart4 = Base64.getUrlEncoder().encodeToString(jwtPart4.getBytes());


        // create jwt
        StringBuilder jwt = new StringBuilder();
        jwt.append(encodedJwtPart1);
        jwt.append(".");
        jwt.append(encodedJwtPart2);
        jwt.append(".");
        jwt.append(encodedJwtPart3);
        jwt.append(".");
        jwt.append(encodedJwtPart4);

        Exception exception = assertThrows(TokenValidationException.class, () -> new Jwt(jwt.toString()));
        Assertions.assertTrue(exception.getMessage().contains("Parsing error"));
    }

    private byte[] signWithRSA256(byte[] data, Key key)
            throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        PrivateKey privateKey = (PrivateKey) key;
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }

    private KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);
        return keyGen.generateKeyPair();
    }
}
