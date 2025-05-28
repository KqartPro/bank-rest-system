package kz.pryakhin.bankrest.exception;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException() {
		super("User not found");
	}
}
