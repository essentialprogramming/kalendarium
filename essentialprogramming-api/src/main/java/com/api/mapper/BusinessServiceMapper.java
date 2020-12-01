package com.api.mapper;

import com.api.entities.BusinessService;
import com.api.input.BusinessServiceInput;
import com.api.output.BusinessServiceJSON;
import com.crypto.Crypt;

import java.security.GeneralSecurityException;
import java.time.format.DateTimeFormatter;

import static com.resources.AppResources.ENCRYPTION_KEY;

public class BusinessServiceMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    public static BusinessService inputToBusinessService(BusinessServiceInput businessServiceInput)
    {
        return BusinessService.builder()
                .name(businessServiceInput.getName())
                .serviceDetail(ServiceDetailMapper.inputToServiceDetail(businessServiceInput))
                .build();
    }

    public static BusinessServiceJSON businessServiceToOutput(BusinessService business) throws GeneralSecurityException {
        return BusinessServiceJSON.builder()
                .name(business.getName())
                .duration(business.getServiceDetail().getDuration())
                .businessServiceCode(Crypt.encrypt(business.getBusinessServiceCode(), ENCRYPTION_KEY.value()))
                .endTime(business.getServiceDetail().getEndTime().format(FORMATTER))
                .startTime(business.getServiceDetail().getStartTime().format(FORMATTER))
                .days(business.getServiceDetail().getDay())
                .build();
    }
}
