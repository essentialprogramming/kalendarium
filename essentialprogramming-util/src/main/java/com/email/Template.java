package com.email;

import java.util.Optional;

public enum Template {

    PARENT_HTML("html/parent"),

    NEW_USER("html/new_user", "new_user", PARENT_HTML),
    APPOINTMENT_CONFIRMATION("html/appointment", "appointment", PARENT_HTML),
    ACTIVATE_ACCOUNT("html/activate_account", "activate_account", PARENT_HTML),
    CONFIRM_ACCOUNT("html/confirm_account", "confirm_account", PARENT_HTML),
    OTP_LOGIN("html/otp_login","otp_login",PARENT_HTML);

    public String page;
    public String fragment = null;
    public Template master = null;

    Template(String page) {
        this.page = page;
    }

    Template(String page, String fragment, Template master) {
        this.page = page;
        this.fragment = fragment;
        this.master = master;
    }

    public Optional<String> getPage() {
        return Optional.of(page);
    }

    public Optional<String> getFragment() {
        return Optional.ofNullable(fragment);
    }

    public Optional<Template> getMaster() {
        return Optional.ofNullable(master);
    }
}
