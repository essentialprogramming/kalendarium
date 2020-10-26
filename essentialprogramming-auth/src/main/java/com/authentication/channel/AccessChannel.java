package com.authentication.channel;

/**
 * Represents the different access channels a user may log in by.
 */
public enum AccessChannel {
    PASSWORD, AUTO, REFRESH_TOKEN, OTP;

    public static AccessChannel getAccessChannel(String param) {

        switch (param) {
            case "auto":
                return AccessChannel.AUTO;
            case "refresh":
                return AccessChannel.REFRESH_TOKEN;
            case "otp":
                return AccessChannel.OTP;
            case "password":
            default:
                return AccessChannel.PASSWORD;
        }

    }
}