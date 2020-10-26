package com.api.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class StateJSON implements Serializable {

    private String name;
    private String abbreviation;
    private String country;
    private String stateCode;
}
