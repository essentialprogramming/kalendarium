package com.api.entities.enums;

public enum Day {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    private int code;

    Day(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
