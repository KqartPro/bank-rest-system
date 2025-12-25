package kz.pryakhin.bankrest.dto.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kz.pryakhin.bankrest.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {

	@NotNull
	private NotificationType type;

	@NotBlank
	@Size(min = 2)
	private String message;

	@NotNull
	private Long userId;


}