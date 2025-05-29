package kz.pryakhin.bankrest.util;

public class CardMaskHelper {
	public static String maskCardNumber(String maskedSuffix) {
		return "**** **** **** " + maskedSuffix;
	}


}
