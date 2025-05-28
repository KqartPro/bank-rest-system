package kz.pryakhin.bankrest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<String> handleException(AuthenticationException ex) {

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authentication Failed " + ex.getMessage());
	}


	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<String> handleGenericException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error " + ex.getMessage());
	}


	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
				errors.put(error.getField(), error.getDefaultMessage())
		);
		return ResponseEntity.badRequest().body(errors);
	}


	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
		return ResponseEntity
				.status(HttpStatus.FORBIDDEN)
				.body("Access Denied: " + ex.getMessage());
	}
}