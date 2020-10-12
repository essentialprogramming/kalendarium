package com.token.validation.crypto;

import com.token.validation.jwt.Jwt;
import com.token.validation.jwt.exception.TokenValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RSAProviderTest {

    private KeyPair keyPair;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        keyPair = generateRSAKeyPair();
    }

    @Test
    void testVerifyRS256()
            throws SignatureException, InvalidKeyException, NoSuchAlgorithmException, TokenValidationException {

        String jwtString = createJWT("RS256", "SHA256withRSA");
        Jwt jwt = new Jwt(jwtString);

        RSAProvider rsaProvider = RSAProvider.getInstance();
        Assertions.assertTrue(rsaProvider.verify(jwt, keyPair.getPublic()));
    }

    @Test
    void testVerifyRS384()
            throws SignatureException, InvalidKeyException, NoSuchAlgorithmException, TokenValidationException {

        String jwtString = createJWT("RS384", "SHA384withRSA");
        Jwt jwt = new Jwt(jwtString);

        RSAProvider rsaProvider = RSAProvider.getInstance();
        Assertions.assertTrue(rsaProvider.verify(jwt, keyPair.getPublic()));
    }

    @Test
    void testVerifyRS512()
            throws SignatureException, InvalidKeyException, NoSuchAlgorithmException, TokenValidationException {

        String jwtString = createJWT("RS512", "SHA512withRSA");
        Jwt jwt = new Jwt(jwtString);

        RSAProvider rsaProvider = RSAProvider.getInstance();
        Assertions.assertTrue(rsaProvider.verify(jwt, keyPair.getPublic()));
    }

    @Test
    void testVerifyNoAlg()
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, TokenValidationException {
        String jwtString = createJWT("NONE", "SHA256withRSA");
        Jwt jwt = new Jwt(jwtString);

        RSAProvider rsaProvider = RSAProvider.getInstance();
        Exception exception = assertThrows(TokenValidationException.class, () -> rsaProvider.verify(jwt, keyPair.getPublic()));
        Assertions.assertTrue(exception.getMessage().contains("Something went wrong on signature validation"));
    }

    @Test
    void testVerifyInvalidPublicKey()
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, TokenValidationException {

        String jwtString = createJWT("RS256", "SHA256withRSA");
        Jwt jwt = new Jwt(jwtString);

        KeyPair keys = generateRSAKeyPair();
        RSAProvider rsaProvider = RSAProvider.getInstance();
        Assertions.assertFalse(rsaProvider.verify(jwt, keys.getPublic()));
    }

    private String createJWT(String jwtAlgorithm, String signAlgorithm)
            throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {

        // create header
        String header = "{ \"alg\" : \"" + jwtAlgorithm + "\", \"typ\" : \"JWT\" }";

        // create paylaod
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        String payload = "{ \"iss\" : \"issuer\", \"iat\" : " + now.getTime() / 1000 + " }";

        // create jwt
        StringBuilder jwt = new StringBuilder();
        jwt.append(Base64.getUrlEncoder().encodeToString(header.getBytes()));
        jwt.append(".");
        jwt.append(Base64.getUrlEncoder().encodeToString(payload.getBytes()));

        // sign jwt
        PrivateKey key = keyPair.getPrivate();
        byte[] sign = sign(jwt.toString().getBytes(), key, signAlgorithm);
        String encodedSignature = Base64.getUrlEncoder().encodeToString(sign);

        jwt.append(".");
        jwt.append(encodedSignature);

        return jwt.toString();
    }

    private byte[] sign(byte[] data, PrivateKey privateKey, String alg)
            throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        Signature sig = Signature.getInstance(alg);
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }

    private KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

}
