package com.api.service;

import com.api.entities.*;
import com.resources.AppResources;
import com.api.mapper.UserMapper;
import com.api.input.*;
import com.api.output.UserJSON;
import com.api.repository.*;
import com.crypto.Crypt;
import com.crypto.PasswordHash;
import com.internationalization.EmailMessages;
import com.internationalization.Messages;
import com.email.EmailTemplateService;
import com.email.Template;
import com.util.enums.HTTPCustomStatus;
import com.util.exceptions.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    private final UserRepository userRepository;
    private final EmailTemplateService emailTemplateService;

    @Autowired
    public UserService(UserRepository userRepository, EmailTemplateService emailTemplateService) {

        this.userRepository = userRepository;
        this.emailTemplateService = emailTemplateService;
    }

    @Transactional
    public UserJSON save(UserInput input, com.util.enums.Language language) throws GeneralSecurityException {

        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new ApiException(Messages.get("USER.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
                );

        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setPhone(input.getPhone());
        user.setModifiedDate(LocalDateTime.now());

        User result = userRepository.save(user);
        user.setCreatedBy(result.getId());

        return UserMapper.userToJson(result);

    }

    @Transactional
    public boolean checkAvailabilityByEmail(String email) {

        Optional<User> user = userRepository.findByEmail(email);
        return !user.isPresent();
    }


    @Transactional
    public UserJSON loadUser(String email, com.util.enums.Language language) throws ApiException {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            logger.info("User loaded={}",email);
            return UserMapper.userToJson(user.get());
        } else
            throw new ApiException(Messages.get("USER.NOT.FOUND", language), HTTPCustomStatus.BUSINESS_VALIDATION_ERROR);

    }


    private User saveUser(User user, UserInput input, com.util.enums.Language language) throws GeneralSecurityException {

        String uuid = String.valueOf(UUID.randomUUID());
        String validationKey = Crypt.encrypt(uuid, Crypt.encrypt(uuid, uuid));  //activationKey
        LocalDateTime now = LocalDateTime.now();

        user.setUserKey(uuid);
        user.setCreatedDate(now);


        userRepository.save(user);
        if (user.getId() > 0) {
            logger.debug("Start password hashing");
            String password = PasswordHash.encode(input.getPassword());
            logger.debug("Finished password hashing");

            user.setPassword(password);
        }

        return user;
    }


}
