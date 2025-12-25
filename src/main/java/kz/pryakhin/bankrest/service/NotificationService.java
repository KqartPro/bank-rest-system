package kz.pryakhin.bankrest.service;

import kz.pryakhin.bankrest.dto.notification.NotificationRequest;
import kz.pryakhin.bankrest.dto.notification.NotificationResponse;
import kz.pryakhin.bankrest.entity.Notification;
import kz.pryakhin.bankrest.exception.NotificationNotFoundException;
import kz.pryakhin.bankrest.mapper.NotificationMapper;
import kz.pryakhin.bankrest.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final NotificationMapper notificationMapper;
	private final UserService userService;


	public NotificationResponse create(NotificationRequest notificationRequest) {
		Notification notification = new Notification(
				notificationRequest.getType(),
				notificationRequest.getMessage(),
				userService.getUserById(notificationRequest.getUserId())
		);
		return notificationMapper.toDto(notificationRepository.save(notification));
	}


	public List<NotificationResponse> getUserNotifications(Long userId) {
		return notificationMapper.toDtoList(notificationRepository.findByUserId(userId));
	}


	public NotificationResponse markAsRead(Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(NotificationNotFoundException::new);

		notification.setRead(true);
		return notificationMapper.toDto(notificationRepository.save(notification));
	}


	public void delete(Long id) {
		notificationRepository.findById(id)
				.orElseThrow(NotificationNotFoundException::new);

		notificationRepository.deleteById(id);
	}
}