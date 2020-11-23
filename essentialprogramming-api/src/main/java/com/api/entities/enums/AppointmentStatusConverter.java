package com.api.entities.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class AppointmentStatusConverter implements AttributeConverter<AppointmentStatus, String> {
    @Override
    public String convertToDatabaseColumn(AppointmentStatus appointmentStatus) {
        if(appointmentStatus == null)
            return null;

        return appointmentStatus.getCode();
    }

    @Override
    public AppointmentStatus convertToEntityAttribute(String code) {
        if(code == null)
            return null;

        return Stream.of(AppointmentStatus.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
