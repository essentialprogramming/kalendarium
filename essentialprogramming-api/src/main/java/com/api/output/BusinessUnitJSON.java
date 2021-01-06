package com.api.output;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessUnitJSON {

    private String name;
    private String businessUnitCode;
    private String businessUnitOwner;

}
