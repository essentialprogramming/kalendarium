package com.authentication.response;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OAuth 2.0 Access Token Response json
 *
 * @author Razvan Prichici
 */

@Schema(name = "AccessToken",
        description = "An AccessToken object is returned for the authenticated user. It contains the JWT Token.")
public class AccessToken {

    @Schema(name = "The Token", required = true)
    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("tokenType")
    private String tokenType;

    @JsonProperty("expireIn")
    private long expiresIn;

    @JsonProperty("expireDate")
    private Date expireAt;

    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("refreshExpiresIn")
    private long refreshExpiresIn;


    @JsonProperty("not-before-policy")
    private int notBeforePolicy;


    @JsonProperty("active")
    private boolean active;


    private final Map<String, Object> otherClaims = new HashMap<>();

    /**
     * The default constructor.
     */
    public AccessToken() {
        super();
    }

    public AccessToken accessToken(final String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public AccessToken expiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public AccessToken expiresAt(Date expireAt) {
        this.expireAt = expireAt;
        return this;
    }

    public AccessToken tokenType(String tokenType) {
        this.tokenType = tokenType;
        return this;
    }

    public AccessToken active(boolean active) {
        this.active = active;
        return this;
    }

    public AccessToken refreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }


    public AccessToken refreshExpiresIn(long refreshExpiresIn) {
        this.refreshExpiresIn = refreshExpiresIn;
        return this;
    }

    public AccessToken notBeforePolicy(int notBeforePolicy) {
        this.notBeforePolicy = notBeforePolicy;
        return this;
    }



    public String getTokenType() {
        return tokenType;
    }
    public boolean isActive() {
        return active;
    }

    @JsonAnySetter
    public void setOtherClaims(String name, Object value) {
        otherClaims.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getOtherClaims() {
        return otherClaims;
    }

}
