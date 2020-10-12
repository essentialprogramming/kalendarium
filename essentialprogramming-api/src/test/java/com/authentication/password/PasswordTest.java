package com.authentication.password;

import com.util.password.PasswordOptions;
import com.util.password.PasswordStrength;
import com.util.password.PasswordUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PasswordTest {
	
	@Test
	void password_validation_6_unique_chars_nonAlphanumeric_lowercase_uppercase_digit() {
		PasswordOptions passwordOptions = buildPasswordOptions(6, false, true);
		String validPassword = "Aa1.bC";
		String invalidPasswordNoDigits = "Aad.bc";
		String validPasswordAlphanumericOnly = "Aa1dbc";
		String invalidPasswordNoLowercase = "da1.bc";
		String invalidPasswordNoUppercase = "da1.bc";
		String invalidPasswordNoNonUniqueChars = "aa1.bC";
		assert PasswordUtil.isValidPassword(validPassword, passwordOptions);
		Assertions.assertFalse(PasswordUtil.isValidPassword(invalidPasswordNoDigits, passwordOptions));
		Assertions.assertTrue(PasswordUtil.isValidPassword(validPasswordAlphanumericOnly, passwordOptions));
		Assertions.assertFalse(PasswordUtil.isValidPassword(invalidPasswordNoLowercase, passwordOptions));
		Assertions.assertFalse(PasswordUtil.isValidPassword(invalidPasswordNoUppercase, passwordOptions));
		Assertions.assertFalse(PasswordUtil.isValidPassword(invalidPasswordNoNonUniqueChars, passwordOptions));
	}

	@Test
	void password_strength() {
		String passwordStrong = "Aa1.bCqwert";
		String passwordMedium = "Aa1.bc";
		String passwordWeak = "Aa1dbc";
		String passwordVeryWeak = "abc";
		String passwordBlank = "";
		
		Assertions.assertTrue(PasswordUtil.isStrongPassword(passwordStrong));
		Assertions.assertFalse(PasswordUtil.isStrongPassword(passwordMedium));
		Assertions.assertEquals(PasswordStrength.Strong, PasswordUtil.getPasswordStrength(passwordStrong));
		Assertions.assertEquals(PasswordStrength.Medium, PasswordUtil.getPasswordStrength(passwordMedium));
		Assertions.assertEquals(PasswordStrength.Weak, PasswordUtil.getPasswordStrength(passwordWeak));
		Assertions.assertEquals(PasswordStrength.VeryWeak, PasswordUtil.getPasswordStrength(passwordVeryWeak));
		Assertions.assertEquals(PasswordStrength.Blank, PasswordUtil.getPasswordStrength(passwordBlank));
	}
	
	private PasswordOptions buildPasswordOptions(int length, boolean requiresNonAlphanumeric, boolean alphaNumericOnly) {
		return new PasswordOptions(length, 6, requiresNonAlphanumeric, true, true, true, alphaNumericOnly);
	}

	@Test
	public void generate_random_password() {
		PasswordOptions passwordOptions = buildPasswordOptions(16, false, true);
		for (int i = 0 ; i < 1000; i++) {
			String password = PasswordUtil.generateRandomPassword(passwordOptions);
			System.out.println("Random password: " + password);
			Assertions.assertEquals(passwordOptions.getRequiredLength(), password.length());
			Assertions.assertTrue(PasswordUtil.isValidPassword(password, passwordOptions));
		}
	}
	
	@Test
	public void generate_random_password_with_non_alphanumeric() {
		PasswordOptions passwordOptions = buildPasswordOptions(16, true, false);
		for (int i = 0 ; i < 1000; i++) {
			String password = PasswordUtil.generateRandomPassword(passwordOptions);
			System.out.println("Random password: " + password);
			String nonAlphanumerics = "@%+\\/'!#$^?:,(){}[]~-_.";

	        boolean containsNonalphanumeric = false;
	        for (int j = 0; j < nonAlphanumerics.length(); j++) {
				if (password.contains(String.valueOf(nonAlphanumerics.charAt(j)))) {
					containsNonalphanumeric = true;
					break;
				}
	        }
	        Assertions.assertTrue(containsNonalphanumeric);
			Assertions.assertEquals(passwordOptions.getRequiredLength(), password.length());
			Assertions.assertTrue(PasswordUtil.isValidPassword(password, passwordOptions));
		}
	}
	
	@Test
	public void generate_random_password_alphanumeric_only() {
		PasswordOptions passwordOptions = buildPasswordOptions(6, false, true);
		for (int i = 0 ; i < 1000; i++) {
			String password = PasswordUtil.generateRandomPassword(passwordOptions);
			System.out.println("Random password: " + password);
			String nonAlphanumerics = "@%+\\/'!#$^?:,(){}[]~-_.";

	        boolean containsNonalphanumeric = false;
	        for (int j = 0; j < nonAlphanumerics.length(); j++) {
				if (password.contains(String.valueOf(nonAlphanumerics.charAt(j)))) {
					containsNonalphanumeric = true;
					break;
				}
	        }
			Assertions.assertFalse(containsNonalphanumeric);
			Assertions.assertEquals(passwordOptions.getRequiredLength(), password.length());
			Assertions.assertTrue(PasswordUtil.isValidPassword(password, passwordOptions));
		}
	}
	
}
