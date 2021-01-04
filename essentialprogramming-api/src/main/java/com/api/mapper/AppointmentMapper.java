package com.api.mapper;

import com.api.entities.Appointment;
import com.api.input.AppointmentInput;
import com.api.output.AppointmentJSON;
import com.crypto.Crypt;

import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.resources.AppResources.ENCRYPTION_KEY;

public class AppointmentMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static Appointment inputToAppointment(AppointmentInput appointmentInput) {

        final String endTime = appointmentInput.getEndTime();
        final String startTime = appointmentInput.getStartTime();
        final String dateString = appointmentInput.getDate();

        LocalTime start = startTime!=null ? LocalTime.parse(startTime, FORMATTER) : null;
        LocalTime end = endTime!=null ? LocalTime.parse(endTime, FORMATTER) : null;
        LocalDate date = dateString!=null ? LocalDate.parse(dateString, DATE_FORMATTER) : null;

        return Appointment.builder()
                .endTime(end)
                .startTime(start)
                .day(appointmentInput.getDay())
                .date(date)
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
                .date(appointment.getDate().toString())
                .build();
    }

}
