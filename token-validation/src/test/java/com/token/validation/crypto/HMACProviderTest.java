package com.token.validation.crypto;

import com.token.validation.jwt.Jwt;
import com.token.validation.jwt.exception.TokenValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;

class HMACProviderTest {

    private SecretKey secretKey;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        secretKey = generateHmacSecretKey();
    }

    @Test
    void testVerifyHS256()
            throws NoSuchAlgorithmException, InvalidKeyException, TokenValidationException {

        String jwt = createJWT("HS256", "HMACSHA256");
        Jwt jwtToken = new Jwt(jwt);

        HMACProvider hmacProvider = HMACProvider.getInstance();
        Assertions.assertTrue(hmacProvider.verify(jwtToken, secretKey));
    }

    @Test
    void testVerifyHS384()
            throws NoSuchAlgorithmException, InvalidKeyException, TokenValidationException {

        String jwt = createJWT("HS384", "HMACSHA384");
        Jwt jwtToken = new Jwt(jwt);

        HMACProvider hmacProvider = HMACProvider.getInstance();
        Assertions.assertTrue(hmacProvider.verify(jwtToken, secretKey));
    }

    @Test
    void testVerifyHS512()
            throws NoSuchAlgorithmException, InvalidKeyException, TokenValidationException {

        String jwt = createJWT("HS512", "HMACSHA512");
        Jwt jwtToken = new Jwt(jwt);

        HMACProvider hmacProvider = HMACProvider.getInstance();
        Assertions.assertTrue(hmacProvider.verify(jwtToken, secretKey));
    }

    @Test
    void testVerifySecretKeyNoAlg()
            throws NoSuchAlgorithmException, InvalidKeyException, TokenValidationException {

        String jwt = createJWT("NONE", "HMACSHA256");
        Jwt jwtToken = new Jwt(jwt);

        HMACProvider hmacProvider = HMACProvider.getInstance();
        Exception exception = assertThrows(TokenValidationException.class, () -> hmacProvider.verify(jwtToken, secretKey));
        Assertions.assertTrue(exception.getMessage().contains("Something went wrong on signature validation"));
    }

    @Test
    void testVerifyInvalidKey()
            throws NoSuchAlgorithmException, InvalidKeyException, TokenValidationException {

        String jwt = createJWT("HS256", "HMACSHA256");
        Jwt jwtToken = new Jwt(jwt);

        SecretKey newSecretKey = generateHmacSecretKey();
        HMACProvider hmacProvider = HMACProvider.getInstance();
        Assertions.assertFalse(hmacProvider.verify(jwtToken, newSecretKey));
    }

    private String createJWT(String jwtAlgorithm, String signAlgorithm)
            throws InvalidKeyException, NoSuchAlgorithmException {

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
        byte[] sign = sign(jwt.toString().getBytes(), signAlgorithm, secretKey);
        String encodedSignature = Base64.getUrlEncoder().encodeToString(sign);

        jwt.append(".");
        jwt.append(encodedSignature);

        return jwt.toString();
    }

    private static byte[] sign(byte[] data, String algorithm, SecretKey key)
            throws InvalidKeyException, NoSuchAlgorithmException {
        Mac mac = Mac.getInstance(algorithm);
        mac.init(key);
        mac.update(data);
        return mac.doFinal();
    }

    private SecretKey generateHmacSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA1");
        return keyGen.generateKey();
    }

}
