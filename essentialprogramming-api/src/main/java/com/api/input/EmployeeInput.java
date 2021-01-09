package com.api.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeInput {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private List<String> businessServiceCodes;
}
