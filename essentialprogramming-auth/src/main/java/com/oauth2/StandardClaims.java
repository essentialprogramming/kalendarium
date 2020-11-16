package com.oauth2;

import java.util.Arrays;
import java.util.List;

/**
 * Standard OpenID Connect Claims
 *
 */
public interface StandardClaims {

    /**
     * Subject - Identifier for the End-User at the Issuer.
     */
    String SUB = "sub";
    /**
     * End-User's full name including all name parts, possibly including titles and suffixes, ordered according to the End-User's locale and preferences.
     */
    String NAME = "name";
    /**
     * Given name(s) or first name(s) of the End-User.
     * Note that in some cultures, people can have multiple given names; all can be present, with the names being separated by space characters.
     */
    String GIVEN_NAME = "given_name";
    /**
     * Surname(s) or last name(s) of the End-User.
     */
    String FAMILY_NAME = "family_name";
    /**
     * Middle name(s) of the End-User.
     */
    String MIDDLE_NAME = "middle_name";
    /**
     * Casual name of the End-User that may or may not be the same as the given_name.
     * For instance, a nickname value of Mike might be returned alongside a given_name value of Michael.
     */
    String NICKNAME = "nickname";
    /**
     * Shorthand name by which the End-User wishes to be referred to
     * This value MAY be any valid JSON string including special characters such as @, /, or whitespace.
     */
    String PREFERRED_USERNAME = "preferred_username";
    /**
     * URL of the End-User's profile page. The contents of this Web page SHOULD be about the End-User.
     */
    String PROFILE = "profile";
    /**
     * 	URL of the End-User's profile picture.
     */
    String PICTURE = "picture";
    /**
     * URL of the End-User's Web page or blog.
     */
    String WEBSITE = "website";
    /**
     * End-User's preferred e-mail address.
     */
    String EMAIL = "email";
    /**
     * True if the End-User's e-mail address has been verified; otherwise false.
     */
    String EMAIL_VERIFIED = "email_verified";
    /**
     * End-User's gender.
     */
    String GENDER = "gender";
    /**
     * End-User's birthday, represented as an ISO 8601:2004  YYYY-MM-DD format.
     * The year MAY be 0000, indicating that it is omitted. To represent only the year, YYYY format is allowed.
     */
    String BIRTHDATE = "birthdate";
    /**
     * Time zone database representing the End-User's time zone. For example, Europe/Berlin or America/New_York.
     */
    String ZONEINFO = "zoneinfo";
    /**
     * End-User's locale, represented as a BCP47 [RFC5646] language tag.
 
     */
    String LOCALE = "locale";
    /**
     * End-User's preferred telephone number.
     */
    String PHONE_NUMBER = "phone_number";
    /**
     * True if the End-User's phone number has been verified; otherwise false.
     */
    String PHONE_NUMBER_VERIFIED = "phone_number_verified";
    /**
     * End-User's preferred postal address. The value of the address member is a JSON [RFC4627] structure containing some or all of the members defined in Section 5.1.1.
     */
    String ADDRESS = "address";
    /**
     * Time the End-User's information was last updated. Its value is a JSON number representing the number of seconds from 1970-01-01T0:0:0Z as measured in UTC until the date/time.
     */
    String UPDATED_AT = "updated_at";

    static List<String> claims() {
        return Arrays.asList(SUB, NAME, GIVEN_NAME, FAMILY_NAME, MIDDLE_NAME, NICKNAME,
                PREFERRED_USERNAME, PROFILE, PICTURE, WEBSITE, EMAIL, EMAIL_VERIFIED,
                GENDER, BIRTHDATE, ZONEINFO, LOCALE, PHONE_NUMBER, PHONE_NUMBER_VERIFIED, ADDRESS, UPDATED_AT);
    }
}
