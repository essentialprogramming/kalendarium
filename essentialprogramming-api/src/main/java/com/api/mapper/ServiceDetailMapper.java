package com.api.mapper;

import com.api.entities.ServiceDetail;
import com.api.input.BusinessServiceInput;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ServiceDetailMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("h:mm a");


    public static ServiceDetail inputToServiceDetail(BusinessServiceInput businessServiceInput)
    {
        return ServiceDetail.builder()
                .duration(businessServiceInput.getDuration())
                .startTime(LocalTime.parse(businessServiceInput.getStartTime(), FORMATTER))
                .endTime(LocalTime.parse(businessServiceInput.getEndTime(), FORMATTER))
                .day(businessServiceInput.getDays())
                .build();
    }
}
