package com.api.mapper;

import com.api.entities.BusinessService;
import com.api.input.BusinessServiceInput;
import com.api.output.BusinessServiceJSON;
import com.crypto.Crypt;

import java.security.GeneralSecurityException;
import java.time.LocalTime;
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
        final LocalTime endTime = business.getServiceDetail().getEndTime();
        String end = endTime !=null ? endTime.format(FORMATTER) : null;

        final LocalTime startTime = business.getServiceDetail().getStartTime();
        String start = startTime !=null ? startTime.format(FORMATTER) : null;


        return BusinessServiceJSON.builder()
                .name(business.getName())
                .duration(business.getServiceDetail().getDuration())
                .businessServiceCode(Crypt.encrypt(business.getBusinessServiceCode(), ENCRYPTION_KEY.value()))
                .endTime(end)
                .startTime(start)
                .days(business.getServiceDetail().getDay())
                .build();
    }
}
