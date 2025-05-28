package kz.pryakhin.bankrest.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
@Slf4j
public class CardNumberEncoder {

	@Value("${card.cipher.key}")
	private String KEY;

	@Value("${card.cipher.init.vector}")
	private String INIT_VECTOR;

	@Value("${card.cipher.algorithm}")
	private String ALGORITHM;


	public String encrypt(String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes());
			SecretKey skeySpec = new SecretKeySpec(KEY.getBytes(), "AES");

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception ex) {
			log.error("Error during encryption", ex);
			throw new RuntimeException("Encryption failed", ex);
		}
	}


	public String decrypt(String encrypted) {
		try {
			IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes());
			SecretKey skeySpec = new SecretKeySpec(KEY.getBytes(), "AES");

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
			return new String(original);
		} catch (Exception ex) {
			throw new RuntimeException("Decryption failed", ex);
		}
	}
}