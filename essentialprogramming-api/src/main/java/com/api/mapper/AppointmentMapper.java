package com.api.mapper;

import com.api.entities.Appointment;
import com.api.input.AppointmentInput;
import com.api.output.AppointmentJSON;

public class AppointmentMapper {

    public static Appointment inputToAppointment(AppointmentInput appointmentInput) {
        return Appointment.builder()
                .status(appointmentInput.getStatus())
                .build();
    }

    public static AppointmentJSON appointmentToOutput(Appointment appointment) {
        return AppointmentJSON.builder()
                .appointmentCode(appointment.getAppointmentCode())
                .businessUnitCode(appointment.getBusinessUnit().getBusinessUnitCode())
                .status(appointment.getStatus())
                .build();
    }

}
