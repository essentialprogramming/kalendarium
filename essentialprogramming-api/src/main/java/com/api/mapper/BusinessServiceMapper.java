package com.api.mapper;

import com.api.entities.BusinessService;
import com.api.input.BusinessServiceInput;
import com.api.output.BusinessServiceJSON;
import com.crypto.Crypt;

import java.security.GeneralSecurityException;

import static com.resources.AppResources.ENCRYPTION_KEY;

public class BusinessServiceMapper {
    public static BusinessService inputToBusinessService(BusinessServiceInput businessServiceInput)
    {
        return BusinessService.builder()
                .name(businessServiceInput.getName())
                .build();
    }

    public static BusinessServiceJSON businessServiceToOutput(BusinessService business) throws GeneralSecurityException {
        return BusinessServiceJSON.builder()
                .name(business.getName())
                .duration(business.getServiceDetail().getDuration())
                .businessServiceCode(Crypt.encrypt(business.getBusinessServiceCode(), ENCRYPTION_KEY.value()))
                .build();
    }
}
