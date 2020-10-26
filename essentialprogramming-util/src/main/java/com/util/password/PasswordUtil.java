package com.util.password;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordUtil {

	public static String generateRandomPassword(PasswordOptions opts) {
		if (opts == null) {
			opts = new PasswordOptions(6, 6, false, true, true, true, false);
		}
		String[] randomChars = new String[] { "ABCDEFGHJKLMNOPQRSTUVWXYZ", // uppercase
				"abcdefghijkmnopqrstuvwxyz", // lowercase
				"0123456789", // digits
				"@%+\\/'!#$^?:,(){}[]~-_." //NonAlphanumeric
		};
		Random rand = new Random();
		List<String> chars = new ArrayList<String>();

		if (opts.isRequireUppercase()) {
			int index = rand.nextInt(randomChars[0].length());
			chars.add(Character.toString(randomChars[0].charAt(index)));
		}
		if (opts.isRequireLowercase()) {
			int index = rand.nextInt(randomChars[1].length());
			chars.add(Character.toString(randomChars[1].charAt(index)));
		}
		if (opts.isRequireDigit()) {
			int index = rand.nextInt(randomChars[2].length());
			chars.add(Character.toString(randomChars[2].charAt(index)));
		}
		if (opts.isRequireNonAlphanumeric()) {
			int index = rand.nextInt(randomChars[3].length());
			chars.add(Character.toString(randomChars[3].charAt(index)));
		}
		for (int i = chars.size() ; i < opts.getRequiredLength() ; i++) {
			chars.add(generateRandomChar(randomChars, rand, opts.isAlphanumericOnly()));
		}
		int duplicatesNumber = 0;
        for (int i = 0; i < chars.size(); i++) {
            for (int j = i + 1; j < chars.size(); j++) {
                if (chars.get(i).equals(chars.get(j))) {
                	duplicatesNumber++;
                }
            }
        }
        List<String> duplicates = new ArrayList<String>();
		if (chars.size() - duplicatesNumber < opts.getRequiredUniqueChars()) {
	        for (int i = 0; i < chars.size(); i++) {
	            for (int j = i + 1; j < chars.size(); j++) {
	                if (chars.get(i).equals(chars.get(j))) {
	                	duplicates.add(chars.get(i));
	                	String generated;
	                	while ((generated = generateRandomChar(randomChars, rand, opts.isAlphanumericOnly())) == chars.get(i) || 
	                	duplicates.contains(String.valueOf(generated)) ||
	                			chars.contains(String.valueOf(generated))) {}
	                	chars.remove(i);
	                	chars.add(i, generated);
	                }
	            }
	        }
		}
		Collections.shuffle(chars);
		return chars.stream().map(e -> e.toString()).reduce("", String::concat);
	}	
	
	private static String generateRandomChar(String[] randomChars, Random rand, boolean alphanumericOnly) {
		int randomCharsMaxIndex = randomChars.length - 1;
		if (alphanumericOnly) {
			randomCharsMaxIndex = randomCharsMaxIndex - 1;
		}
		String rcs = randomChars[rand.nextInt(randomCharsMaxIndex)];
		int randomIndex = rand.nextInt(rcs.length());
		return Character.toString(rcs.charAt(randomIndex));
	}

	public static PasswordStrength getPasswordStrength(String password) {
		int score = 0;
		if (password == null || password.isEmpty())
			return PasswordStrength.Blank;
		if (!hasMinimumLength(password, 5))
			return PasswordStrength.VeryWeak;
		if (hasMinimumLength(password, 8))
			score++;
		if (hasUpperCaseLetter(password) && hasLowerCaseLetter(password))
			score++;
		if (hasDigit(password))
			score++;
		if (hasSpecialChar(password))
			score++;
		return PasswordStrength.get(score);
	}

	/// Sample password policy implementation:
	/// - minimum 8 characters
	/// - at lease one UC letter
	/// - at least one LC letter
	/// - at least one non-letter char (digit OR special char)
	public static boolean isStrongPassword(String password) {
		return hasMinimumLength(password, 8) && hasUpperCaseLetter(password) && hasLowerCaseLetter(password)
				&& (hasDigit(password) || hasSpecialChar(password));
	}


	public static boolean isValidPassword(String password, PasswordOptions opts) {
		return isValidPassword(password, opts.getRequiredLength(), opts.getRequiredUniqueChars(),
				opts.isRequireNonAlphanumeric(), opts.isRequireLowercase(), opts.isRequireUppercase(),
				opts.isRequireDigit());
	}

	private static boolean isValidPassword(String password, int requiredLength, int requiredUniqueChars,
			boolean requireNonAlphanumeric, boolean requireLowercase, boolean requireUppercase, boolean requireDigit) {
		if (!hasLength(password, requiredLength))
			return false;
		if (!hasMinimumUniqueChars(password, requiredUniqueChars))
			return false;
		if (requireNonAlphanumeric && !hasSpecialChar(password))
			return false;
		if (requireLowercase && !hasLowerCaseLetter(password))
			return false;
		if (requireUppercase && !hasUpperCaseLetter(password))
			return false;
		if (requireDigit && !hasDigit(password))
			return false;
		return true;
	}

	private static boolean hasMinimumLength(String password, int minLength) {
		return password.length() >= minLength;
	}

	private static boolean hasLength(String password, int minLength) {
		return password.length() == minLength;
	}
	
	private static boolean hasMinimumUniqueChars(String password, int minUniqueChars) {
		return password.chars().distinct().count() >= minUniqueChars;
	}

	/// Returns TRUE if the password has at least one digit
	private static boolean hasDigit(String password) {
		boolean containsDigit = false;
		if (password != null && !password.isEmpty()) {
			for (char c : password.toCharArray()) {
				if (containsDigit = Character.isDigit(c)) {
					break;
				}
			}
		}
		return containsDigit;
	}

	/// Returns TRUE if the password has at least one special character
	private static boolean hasSpecialChar(String password) {
		// return password.Any(c => char.IsPunctuation(c)) || password.Any(c =>
		// char.IsSeparator(c)) || password.Any(c => char.IsSymbol(c));
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(password);
		return m.find();
	}

	/// Returns TRUE if the password has at least one uppercase letter
	private static boolean hasUpperCaseLetter(String password) {
		return !password.equals(password.toLowerCase());
	}

	/// Returns TRUE if the password has at least one lowercase letter
	private static boolean hasLowerCaseLetter(String password) {
		return !password.equals(password.toUpperCase());
	}

}
