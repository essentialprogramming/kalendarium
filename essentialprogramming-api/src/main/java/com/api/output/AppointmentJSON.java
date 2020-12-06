package com.api.output;

import com.api.entities.enums.AppointmentStatus;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentJSON implements Serializable {

    private AppointmentStatus status;
    private String appointmentCode;
    private String businessUnitCode;

}
