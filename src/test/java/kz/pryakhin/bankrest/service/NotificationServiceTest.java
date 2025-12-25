package kz.pryakhin.bankrest.service;

import kz.pryakhin.bankrest.dto.notification.NotificationRequest;
import kz.pryakhin.bankrest.dto.notification.NotificationResponse;
import kz.pryakhin.bankrest.entity.Notification;
import kz.pryakhin.bankrest.entity.NotificationType;
import kz.pryakhin.bankrest.entity.User;
import kz.pryakhin.bankrest.exception.NotificationNotFoundException;
import kz.pryakhin.bankrest.mapper.NotificationMapper;
import kz.pryakhin.bankrest.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

	@Mock
	private NotificationRepository notificationRepository;

	@Mock
	private NotificationMapper notificationMapper;

	@Mock
	private UserService userService;

	@InjectMocks
	private NotificationService notificationService;

	private Notification notification;
	private NotificationResponse notificationResponse;
	private User user;


	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		user = new User();
		user.setId(1L);
		user.setEmail("user@gmail.com");

		notification = new Notification();
		notification.setId(1L);
		notification.setType(NotificationType.INFO);
		notification.setMessage("Test message");
		notification.setRead(false);
		notification.setUser(user);
		notification.setTimestamp(LocalDateTime.now());

		notificationResponse = new NotificationResponse(
				1L,
				NotificationType.INFO,
				"Test message",
				false,
				LocalDateTime.now(),
				1L
		);
	}


	@Test
	void create_shouldReturnNotificationResponse() {
		NotificationRequest request = new NotificationRequest(NotificationType.INFO, "Test message", 1L);

		when(userService.getUserById(1L)).thenReturn(user);
		when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
		when(notificationMapper.toDto(notification)).thenReturn(notificationResponse);

		NotificationResponse result = notificationService.create(request);

		assertThat(result).isEqualTo(notificationResponse);
		verify(notificationRepository).save(any(Notification.class));
	}


	@Test
	void getUserNotifications_shouldReturnList() {
		when(notificationRepository.findByUserId(1L)).thenReturn(List.of(notification));
		when(notificationMapper.toDtoList(List.of(notification))).thenReturn(List.of(notificationResponse));

		List<NotificationResponse> result = notificationService.getUserNotifications(1L);

		assertThat(result).containsExactly(notificationResponse);
	}


	@Test
	void markAsRead_shouldSetReadTrue() {
		notification.setRead(false);
		NotificationResponse updatedResponse = new NotificationResponse(
				1L,
				NotificationType.INFO,
				"Test message",
				true,
				LocalDateTime.now(),
				1L
		);

		when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
		when(notificationRepository.save(notification)).thenReturn(notification);
		when(notificationMapper.toDto(notification)).thenReturn(updatedResponse);

		NotificationResponse result = notificationService.markAsRead(1L);

		assertThat(result.isRead()).isTrue();
		verify(notificationRepository).save(notification);
	}


	@Test
	void markAsRead_shouldThrowIfNotFound() {
		when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> notificationService.markAsRead(999L))
				.isInstanceOf(NotificationNotFoundException.class);
	}


	@Test
	void delete_shouldCallRepositoryDelete() {
		when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

		notificationService.delete(1L);

		verify(notificationRepository).deleteById(1L);
	}


	@Test
	void delete_shouldThrowIfNotFound() {
		when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> notificationService.delete(999L))
				.isInstanceOf(NotificationNotFoundException.class);
	}
}