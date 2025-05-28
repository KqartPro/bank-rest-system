package kz.pryakhin.bankrest.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardMaskHelper {
	private final CardNumberEncoder cardNumberEncoder;


	public String maskCardNumber(String encryptedCardNumber) {
		String decryptedCardNumber = cardNumberEncoder.decrypt(encryptedCardNumber);

		if (decryptedCardNumber.length() < 4) {
			return "****";
		}

		return "**** **** **** " + decryptedCardNumber.substring(decryptedCardNumber.length() - 4);
	}


}
