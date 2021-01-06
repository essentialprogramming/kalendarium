package com.api.mapper;

import com.api.entities.BusinessUnit;
import com.api.input.BusinessUnitInput;
import com.api.output.BusinessUnitJSON;
import com.crypto.Crypt;

import java.security.GeneralSecurityException;

import static com.resources.AppResources.ENCRYPTION_KEY;

public class BusinessUnitMapper {
    public static BusinessUnit inputToBusinessUnit(BusinessUnitInput businessUnitInput)
    {
        return BusinessUnit.builder()
                .name(businessUnitInput.getName())
                .build();
    }

    public static BusinessUnitJSON businessUnitToOutput(BusinessUnit business) throws GeneralSecurityException {
        return BusinessUnitJSON.builder()
                .name(business.getName())
                .businessUnitCode(Crypt.encrypt(business.getBusinessUnitCode(), ENCRYPTION_KEY.value()))
                .build();
    }
}
