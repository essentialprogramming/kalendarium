package com.api.mapper;

import com.api.entities.Appointment;
import com.api.input.AppointmentInput;
import com.api.output.AppointmentJSON;
import com.crypto.Crypt;

import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.resources.AppResources.ENCRYPTION_KEY;

public class AppointmentMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    public static Appointment inputToAppointment(AppointmentInput appointmentInput) {

        final String endTime = appointmentInput.getEndTime();
        final String startTime = appointmentInput.getStartTime();

        LocalTime start = startTime!=null ? LocalTime.parse(startTime, FORMATTER) : null;
        LocalTime end = endTime!=null ? LocalTime.parse(endTime, FORMATTER) : null;

        return Appointment.builder()
                .status(appointmentInput.getStatus())
                .endTime(end)
                .startTime(start)
                .day(appointmentInput.getDay())
                .build();
    }

    public static AppointmentJSON appointmentToOutput(Appointment appointment) throws GeneralSecurityException {
        return AppointmentJSON.builder()
                .appointmentCode(Crypt.encrypt(appointment.getAppointmentCode(), ENCRYPTION_KEY.value()))
                .businessUnitCode(appointment.getBusinessUnit().getBusinessUnitCode())
                .status(appointment.getStatus())
                .name(appointment.getBusinessService().getName())
                .start(appointment.getStartTime().toString())
                .end(appointment.getEndTime().toString())
                .day(appointment.getDay())
                .build();
    }

}
