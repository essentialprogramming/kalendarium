package com.authentication.security;

import com.authentication.exceptions.codes.ErrorCode;
import com.util.cloud.DeploymentConfiguration;
import com.util.cloud.DeploymentStrategy;
import com.util.cloud.SystemProperty;
import com.util.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.*;

@Service
public class KeyStoreService {

    private static final Logger LOG = LoggerFactory.getLogger(KeyStoreService.class);

    private static final String PUBLIC_KEY_FILE_PATH = "PUBLIC_KEY_FILE_PATH";
    private static final String PRIVATE_KEY_FILE_PATH = "PRIVATE_KEY_FILE_PATH";
    private static final String DEFAULT_PUBLIC_KEY_FILE_PATH = "classpath:pem/public-key.pem";
    private static final String DEFAULT_PRIVATE_KEY_FILE_PATH = "classpath:pem/private-key.pem";
    private static final String DEPLOYMENT_STRATEGY = "DEPLOYMENT_STRATEGY";
    private final String publicKeyFilePath;
    private final String privateKeyFilePath;

    public KeyStoreService() {
        DeploymentStrategy deploymentStrategy = DeploymentStrategy.valueOf(DeploymentConfiguration.getProperty(DEPLOYMENT_STRATEGY, "STANDALONE"));

        final SystemProperty publicKeyConfig = new SystemProperty(PUBLIC_KEY_FILE_PATH, deploymentStrategy);
        publicKeyFilePath = publicKeyConfig.getValue(DEFAULT_PUBLIC_KEY_FILE_PATH);

        final SystemProperty privateKeyConfig = new SystemProperty(PRIVATE_KEY_FILE_PATH, deploymentStrategy);
        privateKeyFilePath = privateKeyConfig.getValue(DEFAULT_PRIVATE_KEY_FILE_PATH);


    }

    public PublicKey getPublicKey() {
        try {
            String pemEncodedRSAPublicKey = PemUtils.readKeyAsString(publicKeyFilePath);
            return PemUtils.getPublicKeyFromPEM(pemEncodedRSAPublicKey);
        } catch (Exception e) {
            LOG.error(ErrorCode.UNABLE_TO_GET_PUBLIC_KEY.getDescription());
            throw new ServiceException(ErrorCode.UNABLE_TO_GET_PUBLIC_KEY, e);
        }
    }

    public PrivateKey getPrivateKey() {
        try {
            String pemEncodedRSAPrivateKey = PemUtils.readKeyAsString(privateKeyFilePath);
            return PemUtils.getPrivateKeyFromPEM(pemEncodedRSAPrivateKey);
        } catch (Exception e) {
            LOG.error(ErrorCode.UNABLE_TO_GET_PRIVATE_KEY.getDescription());
            throw new ServiceException(ErrorCode.UNABLE_TO_GET_PRIVATE_KEY, e);
        }
    }
}
