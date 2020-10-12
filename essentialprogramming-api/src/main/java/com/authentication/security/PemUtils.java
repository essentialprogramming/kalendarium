package com.authentication.security;

import com.authentication.exceptions.codes.ErrorCode;
import com.util.exceptions.ServiceException;
import com.util.io.FileInputResource;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class PemUtils {


    /**
     * Return Public Key loaded from PEM key format
     *
     * @param key public key in PEM format
     * @return Public Key
     */
    public static PublicKey getPublicKeyFromPEM(String key) {

        try {
            // parse and remove header, footer, newlines and whitespaces
            String publicKeyPEM = key.replace("-----BEGIN RSA PUBLIC KEY-----", "")
                    .replace("-----END RSA PUBLIC KEY-----", "").replaceAll("\\s", "");

            byte[] publicKeyDecoded = Base64.getDecoder().decode(publicKeyPEM);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyDecoded));
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.UNABLE_TO_GET_PUBLIC_KEY,
                    "Error trying to load public key from PEM format");
        }
    }


  /**
   * Return Private Key loaded from PEM key format
   * 
   * @param key private Key in PEM format
   * @return privateKey
   */
  public static PrivateKey getPrivateKeyFromPEM(String key) {

        try {
            // parse and remove header, footer, newlines and whitespaces
            String privateKeyPEM = key.replace("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replace("-----END RSA PRIVATE KEY-----", "").replaceAll("\\s", "");

            byte[] privateKeyDecoded = Base64.getDecoder().decode(privateKeyPEM);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyDecoded));
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.UNABLE_TO_GET_PRIVATE_KEY,
                    "Error trying to load private key from PEM format");
        }
    }

    public static String readKeyAsString(String keyLocation) throws Exception {

        FileInputResource fileInputResource = new FileInputResource(keyLocation);
        return new String(fileInputResource.getBytes());
    }
}
