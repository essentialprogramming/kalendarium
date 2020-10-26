package com.api.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class HistoryUserJSON implements Serializable {

    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private int defaultLanguageId;
    private boolean validated;
    private String userKey;
    private boolean active;
    private boolean deleted;
    private LocalDateTime createdDate;
    private int createdBy;
}
