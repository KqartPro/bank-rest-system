package kz.pryakhin.bankrest.controller;

import kz.pryakhin.bankrest.dto.notification.NotificationRequest;
import kz.pryakhin.bankrest.dto.notification.NotificationResponse;
import kz.pryakhin.bankrest.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;


	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public NotificationResponse create(@RequestBody NotificationRequest request) {
		return notificationService.create(request);
	}


	@GetMapping("/users/{userId}")
	@PreAuthorize("hasRole('USER')")
	public List<NotificationResponse> getByUser(@PathVariable Long userId) {
		return notificationService.getUserNotifications(userId);
	}


	@PutMapping("/{id}/read")
	@PreAuthorize("hasRole('USER')")
	public NotificationResponse markRead(@PathVariable Long id) {
		return notificationService.markAsRead(id);
	}


	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public void delete(@PathVariable Long id) {
		notificationService.delete(id);
	}
}