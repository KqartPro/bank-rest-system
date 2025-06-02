package kz.pryakhin.bankrest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.pryakhin.bankrest.dto.card.TransferCreateDto;
import kz.pryakhin.bankrest.dto.card.TransferDto;
import kz.pryakhin.bankrest.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

	@Mock
	private TransferService transferService;

	@InjectMocks
	private TransferController transferController;

	private MockMvc mockMvc;

	private ObjectMapper objectMapper;


	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(transferController).build();
		objectMapper = new ObjectMapper();
	}


	@Test
	void transferBetweenOwnCards_shouldReturnTransferDto() throws Exception {
		TransferCreateDto transferCreateDto = new TransferCreateDto(1L, 2L, new BigDecimal("100.00"));
		TransferDto transferDto = new TransferDto(1L, 2L, new BigDecimal("100.00"), LocalDateTime.now());

		when(transferService.transferBetweenOwnCards(eq(transferCreateDto), any(Principal.class)))
				.thenReturn(transferDto);

		mockMvc.perform(post("/api/v1/transfers/own-cards")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(transferCreateDto))
						.principal(() -> "user@example.com"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.senderCardId").value(1L))
				.andExpect(jsonPath("$.recipientCardId").value(2L))
				.andExpect(jsonPath("$.amount").value(100.00));
	}


	@Test
	void transferBetweenCards_shouldReturnTransferDto() throws Exception {
		TransferCreateDto transferCreateDto = new TransferCreateDto(3L, 4L, new BigDecimal("50.00"));
		TransferDto transferDto = new TransferDto(3L, 4L, new BigDecimal("50.00"), LocalDateTime.now());

		when(transferService.transferBetweenCards(transferCreateDto))
				.thenReturn(transferDto);

		mockMvc.perform(post("/api/v1/transfers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(transferCreateDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.senderCardId").value(3L))
				.andExpect(jsonPath("$.recipientCardId").value(4L))
				.andExpect(jsonPath("$.amount").value(50.00));
	}
}