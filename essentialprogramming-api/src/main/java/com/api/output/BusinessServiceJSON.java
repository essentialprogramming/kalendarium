package com.api.output;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessServiceJSON implements Serializable {

    private String name;
    private String businessServiceCode;
    private long duration;
}
