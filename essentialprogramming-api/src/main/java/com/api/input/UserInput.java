package com.api.input;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;



@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInput {

    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String password;
    private String confirmPassword;
    private String[] roles;


}
