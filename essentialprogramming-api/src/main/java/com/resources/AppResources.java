package com.resources;

import com.util.cloud.ConfigurationManager;

/**
 * Application external resources
 */
@SuppressWarnings("unchecked")
public enum AppResources {

    /**
     * Url to be sent in the confirmation email.
     */
    ACCOUNT_CONFIRMATION_URL {
        public String value() {
            return System.getenv().getOrDefault("APP_URL", configuration.getPropertyAsString("app.url")) + "/confirm";
        }
    },
    /**
     * Url to be sent in the confirm appointment email.
     */
    APPOINTMENT_CONFIRMATION_URL {
        public String value() {
            return System.getenv().getOrDefault("APP_URL", configuration.getPropertyAsString("app.url")) + "/user/reservationlist";
        }
    },
    /**
     * SendGrid Api Key.
     */
    SENDGRID_API_KEY {
        public String value() {
            return System.getenv().getOrDefault("SENDGRID_API_KEY", configuration.getPropertyAsString("sengrid.api.key"));
        }

    },
    /**
     * Base URL of the application;
     */
    APP_URL {
        public String value() {
            return System.getenv().getOrDefault("APP_URL", configuration.getPropertyAsString("app.url"));
        }
    },

    ENCRYPTION_KEY {
        public String value() {
            return System.getenv().getOrDefault("ENCRYPTION_KEY", configuration.getPropertyAsString("encryption.key"));
        }
    },

    DB_HOSTNAME {
        public String value() {
            return System.getenv().getOrDefault("DB_HOSTNAME", configuration.getPropertyAsString("db.hostname"));
        }
    },

    DB_USER {
        public String value() {
            return System.getenv().getOrDefault("DB_USER", configuration.getPropertyAsString("db.user"));
        }
    },

    DB_PASSWORD {
        public String value() {
            return System.getenv().getOrDefault("DB_PASSWORD", configuration.getPropertyAsString("db.password"));
        }
    },
    OTP_LOGIN_URL {
        public String value() {
            return System.getenv().getOrDefault("APP_URL", configuration.getPropertyAsString("app.url")) + "/otp-login";
        }
    };
    private static final com.util.cloud.Configuration configuration = ConfigurationManager.getConfiguration();

    public abstract <T> T value();
	
    }
