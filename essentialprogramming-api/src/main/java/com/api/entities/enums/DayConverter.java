package com.api.entities.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class DayConverter implements AttributeConverter<Day, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Day day) {
        if (day == null)
            return null;

        return day.getCode();
    }

    @Override
    public Day convertToEntityAttribute(Integer code) {
        if(code == null)
            return null;

        return Stream.of(Day.values())
                .filter(c -> c.getCode() == code)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
