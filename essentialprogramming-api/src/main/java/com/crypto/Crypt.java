package com.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.spec.KeySpec;
import java.util.Base64;


public final class Crypt {

    private static final String salt = "supercalifragilisticexpialidocious";
    private final static byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    private Crypt() {
        throw new IllegalAccessError("Instantiation prohibited");
    }

    /**
     * Encrypts a value
     *
     * @param value to encrypt
     * @return encrypted value in base64 format
     */
    public static String encrypt(String value, String secretKey)
            throws GeneralSecurityException {

        IvParameterSpec parameterSpec = new IvParameterSpec(iv);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 128);
        SecretKey temporaryKey = factory.generateSecret(spec);

        Key secretKeySpec = new SecretKeySpec(temporaryKey.getEncoded(), "AES");


        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);

        byte[] utf8 = value.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = cipher.doFinal(utf8);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
    }

    /**
     * Decrypts an encrypted value
     *
     * @param value to decrypt
     * @return decrypted value
     */
    public static String decrypt(String value, String secretKey)
            throws GeneralSecurityException {

        IvParameterSpec parameterSpec = new IvParameterSpec(iv);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 128);
        SecretKey temporaryKey = factory.generateSecret(spec);


        Key aesKey = new SecretKeySpec(temporaryKey.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, parameterSpec);

        byte[] decoded = Base64.getUrlDecoder().decode(value);
        byte[] decrypted = cipher.doFinal(decoded);

        // Decode using utf-8
        return new String(decrypted, StandardCharsets.UTF_8);
    }


}
