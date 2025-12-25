package kz.pryakhin.bankrest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private NotificationType type;

	@Column(nullable = false)
	private String message;

	@Column(nullable = false)
	private boolean read = false;

	@CreationTimestamp
	@Column(updatable = false, nullable = false)
	private LocalDateTime timestamp;

	// Опциональная связь с пользователем
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;


	public Notification(NotificationType type, String message, User user) {
		this.type = type;
		this.message = message;
		this.user = user;
	}
}
