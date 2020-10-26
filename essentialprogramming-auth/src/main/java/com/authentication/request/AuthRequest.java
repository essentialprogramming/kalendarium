package com.authentication.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Authentication", description = "An authentication object that contains the user credentials.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthRequest implements TokenRequest {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


}

