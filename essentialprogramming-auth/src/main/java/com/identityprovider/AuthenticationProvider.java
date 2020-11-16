package com.identityprovider;


import com.identityprovider.entities.Account;
import com.identityprovider.model.PasswordInput;
import com.identityprovider.model.ResetPasswordInput;
import com.authentication.request.AuthRequest;
import com.exceptions.PasswordException;
import com.util.enums.Language;
import com.util.exceptions.ApiException;

import java.io.Serializable;
import java.security.GeneralSecurityException;

public interface AuthenticationProvider {

    Account authenticate(AuthRequest authRequest, Language language) throws ApiException;
    Serializable generateOtp(String email, Language language, String platform) throws ApiException;

    Serializable resetPassword(ResetPasswordInput resetPasswordInput, Language language) throws ApiException, GeneralSecurityException;
    Serializable setPassword(PasswordInput passwordInput, Language language) throws GeneralSecurityException, ApiException, PasswordException;

}
