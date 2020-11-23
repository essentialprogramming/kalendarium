package com.api.entities.enums;

public enum AppointmentStatus {
    PENDING("P"),
    ACCEPTED("A");

    private String code;

    private AppointmentStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
