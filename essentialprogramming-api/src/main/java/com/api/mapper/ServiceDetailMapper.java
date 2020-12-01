package com.api.mapper;

import com.api.entities.ServiceDetail;
import com.api.input.BusinessServiceInput;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ServiceDetailMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("h:mm a");


    public static ServiceDetail inputToServiceDetail(BusinessServiceInput businessServiceInput)
    {
        final String endTime = businessServiceInput.getEndTime();
        final String startTime = businessServiceInput.getStartTime();

        LocalTime start = startTime!=null ? LocalTime.parse(startTime, FORMATTER) : null;
        LocalTime end = endTime!=null ? LocalTime.parse(endTime, FORMATTER) : null;

        return ServiceDetail.builder()
                .duration(businessServiceInput.getDuration())
                .startTime(start)
                .endTime(end)
                .day(businessServiceInput.getDays())
                .build();
    }
}
