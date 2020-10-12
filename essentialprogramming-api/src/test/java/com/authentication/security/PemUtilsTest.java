package com.authentication.security;

import com.util.exceptions.ServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PemUtilsTest {



    @Test
    void get_public_key_from_PEM() throws NoSuchAlgorithmException {
        String publicKeyPem = createPublicKeyPem();

        Assertions.assertNotNull(PemUtils.getPublicKeyFromPEM(publicKeyPem));
    }

    @Test
    void get_public_key_from_PEM_throw_error() {
        String publicKeyPem = "invalid public key";

        Exception exception = assertThrows(ServiceException.class, () -> PemUtils.getPublicKeyFromPEM(publicKeyPem));
        Assertions.assertTrue(exception.getMessage().contains("Error trying to load public key from PEM format"));
    }

    @Test
    void get_private_key_from_PEM() throws NoSuchAlgorithmException {
        String privateKeyPem = createPrivateKeyPem();

        Assertions.assertNotNull(PemUtils.getPrivateKeyFromPEM(privateKeyPem));
    }

    @Test
    void get_private_key_from_PEM_throw_error() {
        String privateKeyPem = "invalid private key";

        Exception exception = assertThrows(ServiceException.class, () -> PemUtils.getPrivateKeyFromPEM(privateKeyPem));
        Assertions.assertTrue(exception.getMessage().contains("Error trying to load private key from PEM format"));
    }

    private String createPublicKeyPem() throws NoSuchAlgorithmException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        String publicKeyText = "-----BEGIN RSA PUBLIC KEY-----\n";
        publicKeyText +=
                Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyPair.getPublic().getEncoded());
        publicKeyText += "\n-----END RSA PUBLIC KEY-----";
        return publicKeyText;
    }

    private String createPrivateKeyPem() throws NoSuchAlgorithmException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        String privateKeyText = "-----BEGIN RSA PRIVATE KEY-----\n";
        privateKeyText += Base64.getMimeEncoder(64, "\n".getBytes())
                .encodeToString(keyPair.getPrivate().getEncoded());
        privateKeyText += "\n-----END RSA PRIVATE KEY-----";
        return privateKeyText;
    }
}
