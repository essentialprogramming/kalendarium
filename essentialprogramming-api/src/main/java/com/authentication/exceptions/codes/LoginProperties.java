package com.authentication.exceptions.codes;

/**
 * Properties for authorization provider.
 *
 * @author Razvan Prichici
 */
public final class LoginProperties {
    /**
     * Hub ID
     */
    public static final String TOKEN_ISSUER = "token.issuer";
    /**
     * Period until the  token expires in hours
     */
    public static final String TOKEN_VALID_PERIOD_HOURS = "token.valid.period.hours";
    /**
     * name of the private keystore file
     */
    public static final String KEYSTORE_PRIVATE = "keystore.private";
    /**
     * password of private keystore
     */
    public static final String KEYSTORE_PRIVATE_PASSWORD = "keystore.private.password";
    /**
     * alias of the private key for JWT Token
     */
    public static final String PRIVATE_KEY_ALIAS = "private.key.alias";
    /**
     * password of the private key for JWT Token
     */
    public static final String PRIVATE_KEY_PASSWORD = "private.key.password";
    /**
     * alias of the symmetric key for JWT Token
     */
    public static final String SYMMETRIC_KEY_ALIAS = "symmetric.key.alias";
    /**
     * password of the symmetric key for JWT Token
     */
    public static final String SYMMETRIC_KEY_PASSWORD = "symmetric.key.password";

    public static final String PROJECT_DOMAIN_DIR = "domain.dir";


    /**
     * Pool size for the fixed thread pool that handles authentication requests.
     */
    public static final String AUTHENTICATION_EXECUTOR_POOL_SIZE = "authentication.executor.pool.size";


    /**
     * Pool size for the fixed thread pool designated to handle external HTTP calls (to various web services).
     */
    public static final String EXTERNAL_HTTP_EXECUTOR_POOL_SIZE = "external.http.executor.pool.size";


    /**
     * Private constructor to hide the default public one.
     */
    private LoginProperties() {
        // Private constructor to hide the default public one.
        throw new IllegalAccessError("Instantiation prohibited");
    }
}