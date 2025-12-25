package kz.pryakhin.bankrest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.pryakhin.bankrest.dto.notification.NotificationRequest;
import kz.pryakhin.bankrest.dto.notification.NotificationResponse;
import kz.pryakhin.bankrest.entity.NotificationType;
import kz.pryakhin.bankrest.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private NotificationController notificationController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;


	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
		objectMapper = new ObjectMapper();
	}


	@Test
	void createNotification_shouldReturnCreatedNotification() throws Exception {
		NotificationRequest request = new NotificationRequest(NotificationType.INFO, "Test message", 1L);
		NotificationResponse response = new NotificationResponse(
				1L,
				NotificationType.INFO,
				"Test message",
				false,
				LocalDateTime.now(),
				1L
		);

		Mockito.when(notificationService.create(any())).thenReturn(response);

		mockMvc.perform(post("/api/v1/notifications")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.type").value("INFO"))
				.andExpect(jsonPath("$.message").value("Test message"))
				.andExpect(jsonPath("$.read").value(false))
				.andExpect(jsonPath("$.userId").value(1));
	}


	@Test
	void getUserNotifications_shouldReturnList() throws Exception {
		List<NotificationResponse> notifications = List.of(
				new NotificationResponse(1L, NotificationType.INFO, "Message 1", false, LocalDateTime.now(), 1L),
				new NotificationResponse(2L, NotificationType.WARNING, "Message 2", true, LocalDateTime.now(), 1L)
		);

		Mockito.when(notificationService.getUserNotifications(1L)).thenReturn(notifications);

		mockMvc.perform(get("/api/v1/notifications/users/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].type").value("INFO"))
				.andExpect(jsonPath("$[1].read").value(true));
	}


	@Test
	void markRead_shouldReturnUpdatedNotification() throws Exception {
		NotificationResponse response = new NotificationResponse(1L, NotificationType.INFO, "Message 1", true, LocalDateTime.now(), 1L);
		Mockito.when(notificationService.markAsRead(1L)).thenReturn(response);

		mockMvc.perform(put("/api/v1/notifications/1/read"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.read").value(true));
	}


	@Test
	void deleteNotification_shouldReturnNoContent() throws Exception {
		mockMvc.perform(delete("/api/v1/notifications/1"))
				.andExpect(status().isOk());
	}
}