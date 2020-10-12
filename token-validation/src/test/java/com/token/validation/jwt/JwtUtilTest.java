package com.token.validation.jwt;

import com.token.validation.auth.AuthUtils;
import com.token.validation.jwt.exception.TokenValidationException;
import com.token.validation.response.ValidationResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.nio.charset.Charset;
import java.security.*;
import java.util.Base64;
import java.util.Date;


class JwtUtilTest {

    private static final String SIGN_RSA_ALG = "SIGN_RSA";
    private static final String SIGN_HMAC_ALG = "SIGN_HMAC";
    private KeyPair keyPair;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        keyPair = generateRSAKeyPair();
        secretKey = generateHmacSecretKey();
    }

    @Test
    void testVerifyWithRS256()
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, TokenValidationException {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Long expiration = (now.getTime() / 1000) + 300;
        String jwt = createJWT("RS256", "SHA256withRSA", SIGN_RSA_ALG, expiration);
        ValidationResponse response = JwtUtil.verifyJwt(jwt, keyPair.getPublic());
        Assertions.assertTrue(response.isValid());
    }

    @Test
    void testVerifyWithHS256()
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, TokenValidationException {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Long expiration = (now.getTime() / 1000) + 300;
        String jwt = createJWT("HS256", "HMACSHA256", SIGN_HMAC_ALG, expiration);
        ValidationResponse response = JwtUtil.verifyJwt(jwt, secretKey);
        Assertions.assertTrue(response.isValid());
    }

    @Test
    void testVerifyExpiredToken()
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, TokenValidationException {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Long expiration = (now.getTime() / 1000) - 300;
        String jwt = createJWT("RS256", "SHA256withRSA", SIGN_RSA_ALG, expiration);
        ValidationResponse response = JwtUtil.verifyJwt(jwt, keyPair.getPublic());
        Assertions.assertFalse(response.isValid());
    }

    @Test
    void testGetHeader()
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Long expiration = (now.getTime() / 1000) + 300;
        String jwt = createJWT("RS256", "SHA256withRSA", SIGN_RSA_ALG, expiration);
        String currentHeader =
                new String(Base64.getUrlDecoder().decode(jwt.split("\\.")[0]), Charset.forName("UTF-8"));

        String header = JwtUtil.getHeader(jwt);
        Assertions.assertEquals(currentHeader, header);
    }

    @Test
    void testGetClaims()
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Long expiration = (now.getTime() / 1000) + 300;
        String jwt = createJWT("RS256", "SHA256withRSA", SIGN_RSA_ALG, expiration);
        String currentClaims =
                new String(Base64.getUrlDecoder().decode(jwt.split("\\.")[1]), Charset.forName("UTF-8"));

        String claims = JwtUtil.getClaimsSet(jwt);
        Assertions.assertEquals(currentClaims, claims);
    }

    @Test
    void testGetJwtFromAuthorization()
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Long expiration = (now.getTime() / 1000) + 300;
        String jwt = createJWT("RS256", "SHA256withRSA", SIGN_RSA_ALG, expiration);
        String authorizationToken = "Bearer " + jwt;

        String result = AuthUtils.extractBearerToken(authorizationToken);
        Assertions.assertEquals(jwt, result);
    }

    @Test
    void testGetJwtFromAuthorizationInvalidFormat()
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Long expiration = (now.getTime() / 1000) + 300;
        String jwt = createJWT("RS256", "SHA256withRSA", SIGN_RSA_ALG, expiration);
        String authorizationToken = "Basic " + jwt;

        String result = AuthUtils.extractBearerToken(authorizationToken);
        Assertions.assertNull(result);
    }

    @Test
    void testGetJwtFromAuthorizationNull() {

        String result = AuthUtils.extractBearerToken(null);
        Assertions.assertNull(result);
    }

    @Test
    void testGetJwtFromAuthorizationInvalidBearer()
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Long expiration = (now.getTime() / 1000) + 300;
        String jwt = createJWT("RS256", "SHA256withRSA", SIGN_RSA_ALG, expiration);
        String authorizationToken = "Bearer" + jwt;

        String result = AuthUtils.extractBearerToken(authorizationToken);
        Assertions.assertNull(result);
    }

    @Test
    void testGetKeyId()
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, TokenValidationException {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Long expiration = (now.getTime() / 1000) + 300;
        String jwt = createJWT("RS256", "SHA256withRSA", SIGN_RSA_ALG, expiration);

        Assertions.assertEquals("123456", JwtUtil.getKeyId(jwt));
    }

    private String createJWT(String jwtAlgorithm, String signAlgorithm, String signKeyType,
                             Long expirationTime)
            throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {

        // create header
        String header = "{ \"alg\" : \"" + jwtAlgorithm + "\", \"typ\" : \"JWT\", \"kid\" : \"123456\" }";

        // create paylaod
        String payload = "{ \"iss\" : \"issuer\", \"exp\" : " + expirationTime + " }";

        // create jwt
        StringBuilder jwt = new StringBuilder();
        jwt.append(Base64.getUrlEncoder().encodeToString(header.getBytes()));
        jwt.append(".");
        jwt.append(Base64.getUrlEncoder().encodeToString(payload.getBytes()));

        // sign jwt
        byte[] sign = new byte[0];
        if (signKeyType.equals(SIGN_RSA_ALG)) {
            PrivateKey key = keyPair.getPrivate();
            sign = signRSA(jwt.toString().getBytes(), key, signAlgorithm);
        }
        if (signKeyType.equals(SIGN_HMAC_ALG)) {
            sign = signHMAC(jwt.toString().getBytes(), signAlgorithm, secretKey);
        }
        String encodedSignature = Base64.getUrlEncoder().encodeToString(sign);

        jwt.append(".");
        jwt.append(encodedSignature);

        return jwt.toString();
    }

    private byte[] signRSA(byte[] data, PrivateKey privateKey, String alg)
            throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        Signature sig = Signature.getInstance(alg);
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }

    private static byte[] signHMAC(byte[] data, String algorithm, SecretKey key)
            throws InvalidKeyException, NoSuchAlgorithmException {
        Mac mac = Mac.getInstance(algorithm);
        mac.init(key);
        mac.update(data);
        return mac.doFinal();
    }

    private KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    private SecretKey generateHmacSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA1");
        return keyGen.generateKey();
    }

}
