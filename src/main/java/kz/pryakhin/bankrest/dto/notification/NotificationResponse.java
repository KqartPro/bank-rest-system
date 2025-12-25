package kz.pryakhin.bankrest.dto.notification;

import kz.pryakhin.bankrest.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
	private Long id;
	private NotificationType type;
	private String message;
	private boolean read;
	private LocalDateTime timestamp;
	private Long userId;
}