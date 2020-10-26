package com.api.output;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class CountryJSON implements Serializable {

    private String name;
    private String defaultLocale;
    private int displayOrder;
    private String countryCode;
    private List<String> states;
}
