package com.authentication.identityprovider;

public final class IdentityProviders {

  private IdentityProviders() {
    throw new IllegalStateException("Utility class");
  }

  public static final String KEYCLOAK = "keycloak";
  public static final String MYSQL = "mysql";

}
