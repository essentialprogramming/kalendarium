package com.api.service;

import com.api.entities.*;
import com.api.input.BusinessInput;
import com.api.mapper.AddressMapper;
import com.api.mapper.BusinessMapper;
import com.api.mapper.CountryMapper;
import com.api.mapper.StateMapper;
import com.api.output.BusinessJSON;
import com.api.repository.*;
import com.crypto.Crypt;
import com.email.EmailTemplateService;
import com.email.Template;
import com.internationalization.EmailMessages;
import com.internationalization.Messages;
import com.util.enums.HTTPCustomStatus;
import com.util.enums.Language;
import com.util.exceptions.ApiException;
import com.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.*;

import static com.resources.AppResources.ENCRYPTION_KEY;

@Service
public class BusinessService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AddressHistoryRepository addressHistoryRepository;
    private final BusinessRepository businessRepository;
    private final BusinessHistoryRepository businessHistoryRepository;
    private final EmailTemplateService emailTemplateService;
    private final BusinessUsersRepository businessUsersRepository;
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;

    @Autowired
    public BusinessService(UserRepository userRepository, AddressRepository addressRepository,
                           AddressHistoryRepository addressHistoryRepository, BusinessRepository businessRepository,
                           BusinessHistoryRepository businessHistoryRepository,
                           EmailTemplateService emailTemplateService, BusinessUsersRepository businessUsersRepository,
                           CountryRepository countryRepository,
                           StateRepository stateRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.addressHistoryRepository = addressHistoryRepository;
        this.businessRepository = businessRepository;
        this.businessHistoryRepository = businessHistoryRepository;
        this.emailTemplateService = emailTemplateService;
        this.businessUsersRepository = businessUsersRepository;
        this.countryRepository = countryRepository;
        this.stateRepository = stateRepository;
    }

    @Transactional
    public Business save(String email, BusinessInput businessInput, Language language) throws ApiException {

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ApiException(Messages.get("USER.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        LocalDateTime now = LocalDateTime.now();

        Address address = AddressMapper.inputToAddress(businessInput);
        address.setCreatedBy(user);
        address.setCreatedDate(now);
        address.setActive(true);

        if (StringUtils.hasText(businessInput.getCountryCode())) {
            Country country = countryRepository.findByCountryCode(businessInput.getCountryCode()).orElseThrow(
                    () -> new ApiException(Messages.get("COUNTRY.NOT.FOUND", language), HTTPCustomStatus.BUSINESS_EXCEPTION)
            );
            address.setCountry(country);
        }
        if (StringUtils.hasText(businessInput.getStateCode())) {
            State state = stateRepository.findByStateCode(businessInput.getStateCode()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "State not found"));
            address.setState(state);
        }

        addressRepository.save(address);

        AddressHistory addressHistory = AddressMapper.addressToHistory(address, now);
        addressHistory.setCreatedBy(user);

        addressHistoryRepository.save(addressHistory);
        //not necessary ?! Happens on next load , to be checked
        //address.addAddressHistory(addressHistory);
        address.setLatestAddressHistory(addressHistory);

        Business business = BusinessMapper.inputToBusiness(businessInput);
        business.setCreatedBy(user);
        business.setCreatedDate(now);
        business.setAddress(address);
        business.setValidated(true);
        business.setBusinessCode(getComplexUUID());

        //save business
        BusinessHistory businessHistory = BusinessMapper.businessToHistory(business);
        //save history
        businessHistoryRepository.save(businessHistory);

        //not necessary !? Happens on next load , to be checked
        business.addBusinessHistory(businessHistory);
        business.setLatestBusinessHistory(businessHistory);

        businessRepository.save(business);

        BusinessUsers businessUser = new BusinessUsers(business, user);
        businessUser.setCreatedDate(now);
        businessUser.setBusiness(business);
        businessUser.setUser(user);
        businessUsersRepository.save(businessUser);

        user.setActive(true);
        return business;
    }

    private String getComplexUUID() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }

    @Transactional
    public void update(String email, BusinessInput businessInput, Language language) throws ApiException {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ApiException(Messages.get("USER.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );
        Business business = getBusinessForUser(user, language);

        final LocalDateTime now = LocalDateTime.now();

        Address address = business.getAddress();
        AddressMapper.setAddressInfo(address, businessInput);

        address.setModifiedDate(now);
        address.setModifiedBy(user);

        if (StringUtils.hasText(businessInput.getCountryCode())) {
            Country country = countryRepository.findByCountryCode(businessInput.getCountryCode()).orElseThrow(
                    () -> new ApiException(Messages.get("COUNTRY.NOT.FOUND", language), HTTPCustomStatus.BUSINESS_EXCEPTION)
            );
            address.setCountry(country);
        }

        if (StringUtils.hasText(businessInput.getStateCode())) {
            State state = stateRepository.findByStateCode(businessInput.getStateCode()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "State not found"));
            address.setState(state);
        }

        //address history update
        AddressHistory addressHistory = AddressMapper.addressToHistory(address, now);
        addressHistory.setCreatedBy(user);
        addressHistory = addressHistoryRepository.save(addressHistory);
        //address.addAddressHistory(addressHistory);
        address.setLatestAddressHistory(addressHistory);

        BusinessMapper.setBusinessInfo(business, businessInput);
        business.setModifiedDate(now);
        business.setModifiedBy(user);

        //update business history 
        BusinessHistory businessHistory = BusinessMapper.businessToHistory(business);
        businessHistory = businessHistoryRepository.save(businessHistory);

        //business.addBusinessHistory(businessHistory);
        business.setLatestBusinessHistory(businessHistory);

        businessRepository.save(business);
    }


    @Transactional
    public Serializable load(final String email, Language language) throws ApiException, GeneralSecurityException {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ApiException(Messages.get("USER.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );
        Business business = getBusinessForUser(user, language);
        return makeBusinessJSON(business);
    }
    

    
    private int getAddressState(Business business) {
        if (business.getAddress().getState() == null || business.getAddress().getState().getId() == 0)
            return -1;
        return business.getAddress().getState().getId();
    }
    private int getAddressCountry(Business business) {
        if (business.getAddress().getCountry() == null || business.getAddress().getCountry().getId() == 0)
            return -1;
        return business.getAddress().getCountry().getId();
    }

    private BusinessJSON makeBusinessJSON(Business business) throws GeneralSecurityException {
        BusinessJSON result = BusinessMapper.businessToOutput(business);


        if (business.getAddress().getCountry() != null) {
            result.setCountry(CountryMapper.countryToJSON(business.getAddress().getCountry()));
        }

        if (business.getAddress().getState() != null) {
            result.setState(StateMapper.stateToJSON(business.getAddress().getState()));
        }
        return result;
    }
    


    @Transactional
    public Serializable loadDetails(final String code, Language language, int version) throws ApiException, GeneralSecurityException {
        final String businessCode;
        final boolean encrypted = (version == 0);

        if (StringUtils.isEmpty(code)) {
            throw new ApiException(Messages.get("MISSING_BUSINESS_CODE", Language.ENGLISH), HTTPCustomStatus.INVALID_REQUEST);
        }

        businessCode = encrypted ? Crypt.decrypt(code, ENCRYPTION_KEY.value()) : code;
        Business business = businessRepository.findByBusinessCodeAndDeleted(businessCode, false).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESS.NOT.FOUND", language), HTTPCustomStatus.BUSINESS_EXCEPTION)
        );

        return makeBusinessJSON(business);
    }

    @Transactional
    public void sendConfirmationEmail(String userKey, Language language) throws ApiException {
        Optional<User> userOptional = userRepository.findByUserKey(userKey);
        if (!userOptional.isPresent()) {
            throw new ApiException(Messages.get("USER.NOT.FOUND", language), HTTPCustomStatus.BUSINESS_EXCEPTION);
        }

        User user = userOptional.get();
        Map<String, Object> templateKeysAndValues = new HashMap<>();
        templateKeysAndValues.put("fullName", user.getFullName());
        emailTemplateService.send(templateKeysAndValues, user.getEmail(), EmailMessages.get("confirm_account.subject", language.getLocale()), Template.CONFIRM_ACCOUNT, language.getLocale());
    }



    @Transactional


    Business getBusinessForUser(User user, Language language) {

        List<BusinessUsers> businessUsers = user.getBusinessUsers();
        Business business = businessUsers.size() > 0 ? user.getBusinessUsers().get(0).getBusiness() : null;

        if (business == null) {
            throw new ApiException(Messages.get("BUSINESS.NOT.FOUND", language), HTTPCustomStatus.BUSINESS_EXCEPTION);
        }
        return business;
    }
}
