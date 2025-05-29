package kz.pryakhin.bankrest.exception;

public class CardNotFoundException extends RuntimeException {
	public CardNotFoundException(String message) {
		super(message);
	}


	public CardNotFoundException() {
		super("Card not found");
	}
}
