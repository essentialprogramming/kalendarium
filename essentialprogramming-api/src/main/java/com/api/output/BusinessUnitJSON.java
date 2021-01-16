package com.api.output;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessUnitJSON {

    private String name;
    private String businessUnitCode;
    private String businessUnitOwner;
    private List<String> services;

}
