package com.api.mapper;

import com.api.entities.Appointment;
import com.api.input.AppointmentInput;
import com.api.output.AppointmentJSON;
import com.crypto.Crypt;

import java.security.GeneralSecurityException;

import static com.resources.AppResources.ENCRYPTION_KEY;

public class AppointmentMapper {

    public static Appointment inputToAppointment(AppointmentInput appointmentInput) {
        return Appointment.builder()
                .status(appointmentInput.getStatus())
                .build();
    }

    public static AppointmentJSON appointmentToOutput(Appointment appointment) throws GeneralSecurityException {
        return AppointmentJSON.builder()
                .appointmentCode(Crypt.encrypt(appointment.getAppointmentCode(), ENCRYPTION_KEY.value()))
                .businessUnitCode(appointment.getBusinessUnit().getBusinessUnitCode())
                .status(appointment.getStatus())
                .build();
    }

}
