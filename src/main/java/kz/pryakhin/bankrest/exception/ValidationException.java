package kz.pryakhin.bankrest.exception;

public class ValidationException extends RuntimeException {
	public ValidationException(String message) {
		super(message);
	}
}
