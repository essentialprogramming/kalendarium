package com.token.validation.crypto;

import com.token.validation.jwt.Jwt;
import com.token.validation.jwt.exception.TokenValidationException;
import com.token.validation.signature.SignatureAlgorithm;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HMACProvider implements SignatureProvider{

    private HMACProvider() {}

    private static class HMACProviderHolder {
        static final HMACProvider INSTANCE = new HMACProvider();
    }

    public static HMACProvider getInstance() {
        return HMACProviderHolder.INSTANCE;
    }

    private static String getJavaAlgorithm(SignatureAlgorithm alg) {
        switch (alg) {
            case HS256:
                return "HMACSHA256";
            case HS384:
                return "HMACSHA384";
            case HS512:
                return "HMACSHA512";
            default:
                throw new IllegalArgumentException("Not a MAC Algorithm");
        }
    }

    private static boolean verify(Jwt input, SecretKey key) throws TokenValidationException {
        try {
            byte[] signature =
                    sign((input.getEncodedHeader() + '.' + input.getEncodedContent()).getBytes("UTF-8"),
                            input.getHeader().getAlgorithm(), key);
            return MessageDigest.isEqual(signature,
                    Base64.getUrlDecoder().decode(input.getEncodedSignature()));
        } catch (Exception e) {
            throw new TokenValidationException("Something went wrong on signature validation");
        }
    }

    public boolean verify(Jwt input, Key key) throws TokenValidationException {
        return verify(input, (SecretKey) key);
    }

    private static byte[] sign(byte[] data, SignatureAlgorithm algorithm, SecretKey key)
            throws InvalidKeyException, NoSuchAlgorithmException {
        Mac mac = Mac.getInstance(getJavaAlgorithm(algorithm));
        mac.init(key);
        mac.update(data);
        return mac.doFinal();
    }
}
