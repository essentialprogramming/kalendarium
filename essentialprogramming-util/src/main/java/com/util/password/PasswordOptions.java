package com.util.password;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordOptions {

    //     Gets or sets the minimum length a password must be. Defaults to 6.
    public int requiredLength;

    //     Gets or sets the minimum number of unique chars a password must comprised of.
    //     Defaults to 1.
    public int requiredUniqueChars;

    //     Gets or sets a flag indicating if passwords must contain a non-alphanumeric character.
    //     Defaults to true.
    //
    // Value:
    //     True if passwords must contain a non-alphanumeric character, otherwise false.
    public boolean requireNonAlphanumeric;

    // Summary:
    //     Gets or sets a flag indicating if passwords must contain a lower case ASCII character.
    //     Defaults to true.
    //
    // Value:
    //     True if passwords must contain a lower case ASCII character.
    public boolean requireLowercase;

    // Summary:
    //     Gets or sets a flag indicating if passwords must contain a upper case ASCII character.
    //     Defaults to true.
    //
    // Value:
    //     True if passwords must contain a upper case ASCII character.
    public boolean requireUppercase;

    // Summary:
    //     Gets or sets a flag indicating if passwords must contain a digit. Defaults to
    //     true.
    //
    // Value:
    //     True if passwords must contain a digit.
    public boolean requireDigit;
    
    public boolean alphanumericOnly;
    
}
