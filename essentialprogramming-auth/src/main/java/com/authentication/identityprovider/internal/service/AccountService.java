package com.authentication.identityprovider.internal.service;

import com.authentication.env.resources.AppResources;
import com.authentication.identityprovider.internal.entities.*;
import com.authentication.identityprovider.AuthenticationProvider;
import com.authentication.identityprovider.internal.model.PasswordInput;
import com.authentication.identityprovider.internal.model.ResetPasswordInput;
import com.authentication.identityprovider.internal.repository.*;
import com.authentication.request.AuthRequest;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.crypto.Crypt;
import com.crypto.PasswordHash;
import com.api.exceptions.PasswordException;
import com.internationalization.EmailMessages;
import com.internationalization.Messages;
import com.email.EmailTemplateService;
import com.email.Template;
import com.util.enums.HTTPCustomStatus;
import com.util.enums.Language;
import com.util.exceptions.ApiException;
import com.util.password.PasswordStrength;
import com.util.password.PasswordUtil;
import com.web.json.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.*;

import static com.authentication.env.resources.AppResources.OTP_LOGIN_URL;


@Service
public class AccountService implements AuthenticationProvider {

    private final AccountRepository accountRepository;
    private final EmailTemplateService emailTemplateService;

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          EmailTemplateService emailTemplateService) {
        this.accountRepository = accountRepository;
        this.emailTemplateService = emailTemplateService;
    }

    /**
     * Authenticate with the username and password .
     *
     * @param authRequest AuthRequest
     * @return Account for the username after authentication.
     */
    @Override
    public Account authenticate(AuthRequest authRequest, Language language) throws ApiException {

        boolean isValidPassword;
        Optional<Account> account = getAccount(authRequest.getUsername());
        if (account.isPresent()) {
            if (account.get().isDeleted()) {
                throw new ApiException(Messages.get("USER.ACCOUNT.DELETED", language), HTTPCustomStatus.BUSINESS_EXCEPTION);
            }

            isValidPassword = PasswordHash.matches(authRequest.getPassword(), account.get().getPassword());
        } else {
            throw new ApiException(Messages.get("USER.NOT.EXIST", language), HTTPCustomStatus.BUSINESS_EXCEPTION);
        }
        if (isValidPassword) {
            return account.get();
        }
        throw new ApiException(Messages.get("USER.PASSWORD.INVALID", language), HTTPCustomStatus.BUSINESS_EXCEPTION);
    }

    private Optional<Account> getAccount(String username) {
        return accountRepository.findByEmail(username);
    }

    public Serializable setPassword(PasswordInput passwordInput, Language language) throws GeneralSecurityException, ApiException, PasswordException {

        String decryptedUserKey = Crypt.decrypt(passwordInput.getKey(), AppResources.ENCRYPTION_KEY.value());
        Optional<Account> account = accountRepository.findByUserKey(decryptedUserKey);

        if (!account.isPresent()) {
            throw new ApiException(Messages.get("USER.NOT.EXIST", Language.ENGLISH), HTTPCustomStatus.BUSINESS_EXCEPTION);
        }
        if (account.get().isDeleted()) {
            throw new ApiException(Messages.get("USER.ACCOUNT.DELETED", language), HTTPCustomStatus.BUSINESS_EXCEPTION);
        }

        if (!passwordInput.getNewPassword().equals(passwordInput.getConfirmPassword())) {
            throw new ApiException(Messages.get("USER.PASSWORD.DONT.MATCH", language), HTTPCustomStatus.BUSINESS_EXCEPTION);
        }

        PasswordStrength passwordPower = PasswordUtil.getPasswordStrength(passwordInput.getNewPassword());
        boolean passwordStrength = PasswordUtil.isStrongPassword(passwordInput.getNewPassword());

        if (!passwordStrength)
            throw new PasswordException(Messages.get("USER.PASSWORD.STRENGTH", language), PasswordStrength.get(passwordPower.getValue()));

        account.ifPresent(user -> user.setPassword(PasswordHash.encode(passwordInput.getNewPassword())));


        return new JsonResponse()
                .with("status", "ok")
                .done();
    }


    public Serializable generateOtp(String email, Language language, String platform) throws ApiException {
        String otp = NanoIdUtils.randomNanoId();

        Account account = accountRepository.findByEmail(email).orElseThrow(() ->
                new ApiException(Messages.get("USER.NOT.FOUND", language), HTTPCustomStatus.BUSINESS_EXCEPTION));


        String url = OTP_LOGIN_URL.value() + "?email=" + account.getEmail() + "&otp=" + otp + "&platform=" + platform;

        Map<String, Object> templateKeysAndValues = new HashMap<>();
        templateKeysAndValues.put("fullName", account.getFullName());
        templateKeysAndValues.put("link", url);
        emailTemplateService.send(templateKeysAndValues, account.getEmail(), EmailMessages.get("otp_login.subject", language.getLocale()), Template.OTP_LOGIN, language.getLocale());

        return new JsonResponse()
                .with("status", "ok")
                .done();
    }

    @Override
    public Serializable resetPassword(ResetPasswordInput resetPasswordInput, Language language) throws ApiException {
        return null;
    }


}
