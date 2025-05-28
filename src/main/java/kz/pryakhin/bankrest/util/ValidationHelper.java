package kz.pryakhin.bankrest.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationHelper {
	private static final String REGEX_EMAIL = "^[a-z0-9A-Z._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
	private static final String REGEX_NAME = "^[A-Za-zА-Яа-яёЁ]{2,30}([A-Za-zА-Яа-яёЁ]{2,30})?$";
	private static final String REGEX_PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,20}$";


	public static boolean isEmailValid(String email) {
		Pattern pattern = Pattern.compile(REGEX_EMAIL, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}


	public static boolean isNameValid(String name) {
		Pattern pattern = Pattern.compile(REGEX_NAME, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}


	public static boolean isPasswordValid(String password) {
		Pattern pattern = Pattern.compile(REGEX_PASSWORD);
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}
}
