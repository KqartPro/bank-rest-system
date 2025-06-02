package kz.pryakhin.bankrest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.pryakhin.bankrest.dto.card.CardChangeDto;
import kz.pryakhin.bankrest.dto.card.CardDto;
import kz.pryakhin.bankrest.service.CardService;
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
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

	@Mock
	private CardService cardService;

	@InjectMocks
	private CardController cardController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;


	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();
		objectMapper = new ObjectMapper();
	}


	@Test
	void getUserCards_shouldReturnCards() throws Exception {
		List<CardDto> cards = List.of(
				new CardDto(1L, "**** **** **** 1234", BigDecimal.valueOf(1000)),
				new CardDto(2L, "**** **** **** 5678", BigDecimal.valueOf(2000))
		);

		when(cardService.getUserCards(1L, 0, 10, null)).thenReturn(cards);

		mockMvc.perform(get("/api/v1/users/1/cards"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].maskedNumber").value("**** **** **** 1234"));
	}


	@Test
	void createCard_shouldReturnCreatedCard() throws Exception {
		CardChangeDto changeDto = new CardChangeDto();
		changeDto.setBalance(BigDecimal.valueOf(1000));
		CardDto cardDto = new CardDto(1L, "**** **** **** 3456", BigDecimal.valueOf(1000));

		when(cardService.createCard(changeDto)).thenReturn(cardDto);

		mockMvc.perform(post("/api/v1/cards")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(changeDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.maskedNumber").value("**** **** **** 3456"));
	}


	@Test
	void activateCard_shouldReturnActivatedCard() throws Exception {
		CardDto activated = new CardDto(1L, "**** **** **** 1234", BigDecimal.valueOf(1000));

		when(cardService.activateCard(1L)).thenReturn(activated);

		mockMvc.perform(put("/api/v1/cards/1/activate"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}


	@Test
	void getMyCards_shouldReturnUserCards() throws Exception {
		Principal principal = () -> "andrey@gmail.com";
		List<CardDto> cards = List.of(
				new CardDto(1L, "**** **** **** 1111", BigDecimal.valueOf(500))
		);

		when(cardService.getMyCards(0, 10, null, principal)).thenReturn(cards);

		mockMvc.perform(get("/api/v1/cards/my").principal(principal))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1));
	}


	@Test
	void getMyCardBalance_shouldReturnBalance() throws Exception {
		Principal principal = () -> "andrey@gmail.com";

		when(cardService.getMyCardBalance(1L, principal)).thenReturn(BigDecimal.valueOf(1234.56));

		mockMvc.perform(get("/api/v1/cards/my/1/balance").principal(principal))
				.andExpect(status().isOk())
				.andExpect(content().string("1234.56"));
	}


	@Test
	void deleteCard_shouldReturnNoContent() throws Exception {
		doNothing().when(cardService).deleteCard(1L);

		mockMvc.perform(delete("/api/v1/cards/1"))
				.andExpect(status().isNoContent());
	}


	@Test
	void blockMyCardRequest_shouldReturnUpdatedCard() throws Exception {
		Principal principal = () -> "andrey@gmail.com";
		CardDto updated = new CardDto(1L, "**** **** **** 1234", BigDecimal.valueOf(1000));

		when(cardService.blockMyCardRequest(1L, principal)).thenReturn(updated);

		mockMvc.perform(put("/api/v1/cards/my/1/request-block").principal(principal))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}
}