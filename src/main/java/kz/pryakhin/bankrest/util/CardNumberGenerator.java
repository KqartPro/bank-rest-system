package kz.pryakhin.bankrest.util;

public class CardNumberGenerator {

	public static String generateCardNumber(String bin, int length) {
		StringBuilder cardNumber = new StringBuilder(bin);

		while (cardNumber.length() < length - 1) {
			cardNumber.append((int) (Math.random() * 10));
		}

		cardNumber.append(getCheckDigit(cardNumber.toString()));
		return cardNumber.toString();
	}


	private static int getCheckDigit(String number) {
		int sum = 0;
		for (int i = 0; i < number.length(); i++) {
			int digit = Character.getNumericValue(number.charAt(number.length() - 1 - i));
			if (i % 2 == 0) {
				digit *= 2;
				if (digit > 9) digit -= 9;
			}
			sum += digit;
		}
		return (10 - (sum % 10)) % 10;
	}


}