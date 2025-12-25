package kz.pryakhin.bankrest.exception;

public class NotificationNotFoundException extends RuntimeException {
	public NotificationNotFoundException(String message) {
		super(message);
	}


	public NotificationNotFoundException() {
		super("Notification not found");
	}
}
