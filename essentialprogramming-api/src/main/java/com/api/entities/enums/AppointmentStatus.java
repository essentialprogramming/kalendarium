package com.api.entities.enums;

public enum AppointmentStatus {
    PENDING(1),
    ACCEPTED(2);

    private int code;

    private AppointmentStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
