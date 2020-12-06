package com.api.input;

import com.api.entities.enums.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.Column;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentInput {

    private String businessCode;
    private String businessUnitCode;
    private String businessServiceCode;
    private String userKey;
    private AppointmentStatus status;

}
