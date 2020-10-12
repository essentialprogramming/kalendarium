package com.email;

import com.util.exceptions.ErrorCodes;

public enum TemplateErrorCode implements ErrorCodes.ErrorCode {

    UNABLE_TO_SEND_EMAIL(70, "Unable to send email"),
    UNABLE_TO_GENERATE_EMAIL_TEMPLATE(80, "Unable to generate email template"),
    UNABLE_TO_GENERATE_QR_CODE(90, "Unable to generate QR code"),
    UNABLE_TO_GENERATE_PDF(100, "Unable to generate PDF"),
    UNABLE_TO_GENERATE_CSV(110, "Unable to generate CSV");

    private final long code;
    private final String description;

    static {
        ErrorCodes.registerErrorCodes(TemplateErrorCode.class);
    }

    TemplateErrorCode(long code, String description) {
        this.code = code;
        this.description = description;
    }

    public long getCode() {
        return this.code;
    }

    public String getDescription() {
        return description;
    }
}
