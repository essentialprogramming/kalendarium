package com.api.output;

import com.api.entities.enums.Day;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessServiceJSON implements Serializable {

    private String name;
    private String businessServiceCode;
    private long duration;
    private String startTime;
    private String endTime;
    private List<Day> days;
}
