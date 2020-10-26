package com.api.output;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessJSON implements Serializable {

    private String name;
    private String businessCode;
    private String address;
    private String zip;
    private String city;
    private String phone;
    private String email;
    private String linkToMenu;
    private String responsible;
    private String menuType;
    private String fileName;
    private CountryJSON country;
    private StateJSON state;
}
