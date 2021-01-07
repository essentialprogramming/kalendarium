package com.api.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessInput {

    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private String zip;
    private String city;
    private String phone;
    private String email;
    private String countryCode;
    private String stateCode;
}
