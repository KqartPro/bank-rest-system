package kz.pryakhin.bankrest.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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


	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		Throwable cause = ex.getCause();

		if (cause instanceof InvalidFormatException invalidFormatException &&
				invalidFormatException.getTargetType().isEnum()) {

			String invalidValue = invalidFormatException.getValue().toString();
			Class<?> enumClass = invalidFormatException.getTargetType();
			String validValues = Arrays.stream(enumClass.getEnumConstants())
					.map(Object::toString)
					.collect(Collectors.joining(", "));

			String message = "Invalid value: '" + invalidValue + "'. Expected one of: [" + validValues + "]";
			return ResponseEntity.badRequest().body(Map.of("error", message));
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
				"error", "Malformed JSON or invalid field format"
		));
	}
}