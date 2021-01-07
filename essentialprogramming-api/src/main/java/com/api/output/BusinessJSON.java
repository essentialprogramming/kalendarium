package com.api.output;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessJSON implements Serializable {

    private String name;
    private String businessCode;
    private String address;
    private String latitude;
    private String longitude;
    private String zip;
    private String city;
    private String phone;
    private String email;
    private String responsible;
    private CountryJSON country;
    private StateJSON state;
}
