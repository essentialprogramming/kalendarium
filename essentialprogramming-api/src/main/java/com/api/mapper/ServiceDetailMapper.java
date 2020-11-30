package com.api.mapper;

import com.api.entities.ServiceDetail;
import com.api.input.BusinessServiceInput;

public class ServiceDetailMapper {

    public static ServiceDetail inputToServiceDetail(BusinessServiceInput businessServiceInput)
    {
        return ServiceDetail.builder()
                .duration(businessServiceInput.getDuration())
                .build();
    }
}
