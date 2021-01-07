package com.api.mapper;

import com.api.entities.Business;
import com.api.entities.history.BusinessHistory;
import com.api.input.BusinessInput;
import com.api.output.BusinessJSON;
import com.crypto.Crypt;
import com.util.web.URLHelper;

import java.security.GeneralSecurityException;
import java.util.List;

import static com.resources.AppResources.ENCRYPTION_KEY;


public class BusinessMapper {

    public static Business inputToBusiness(BusinessInput businessInput) {
        return Business.builder()
                .name(businessInput.getName())
                .phone(businessInput.getPhone())
                .email(businessInput.getEmail())
                .latitude(businessInput.getLatitude())
                .longitude(businessInput.getLongitude())
                .build();
    }

    public static BusinessHistory businessToHistory(Business business) {
        return BusinessHistory.builder()
                .active(business.isActive())
                .address(business.getAddress())
                .business(business)
                .businessCode(business.getBusinessCode())
                .deleted(business.isDeleted())
                .email(business.getEmail())
                .name(business.getName())
                .phone(business.getPhone())
                .createdBy(business.getCreatedBy())
                .createdDate(business.getCreatedDate())
                .build();
    }

    public static BusinessJSON businessToOutput(Business business) throws GeneralSecurityException {
        return BusinessJSON.builder()
                .name(business.getName())
                .businessCode(Crypt.encrypt(business.getBusinessCode(), ENCRYPTION_KEY.value()))
                .address(business.getAddress().getStreet())
                .zip(business.getAddress().getZip())
                .city(business.getAddress().getCity())
                .phone(business.getPhone())
                .email(business.getEmail())
                .latitude(business.getLatitude())
                .longitude(business.getLongitude())
                .responsible(business.getCreatedBy().getFullName())
                .build();
    }

    public static void setBusinessInfo(Business business, BusinessInput businessInput) {
        business.setName(businessInput.getName());
        business.setPhone(businessInput.getPhone());
        business.setEmail(businessInput.getEmail());
        business.setLongitude(businessInput.getLongitude());
        business.setLatitude(businessInput.getLatitude());
    }

    private static String getFileName(String url) {
        List<String> pathParameters = URLHelper.getPathParametersFromUrl(url);
        return URLHelper.getFileName(pathParameters);
    }

}
