package com.api.input;

import com.api.entities.enums.Day;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessServiceUpdateInput {
    private String name;
    private long duration;
    private String startTime;
    private String endTime;
    private List<Day> days;
}
