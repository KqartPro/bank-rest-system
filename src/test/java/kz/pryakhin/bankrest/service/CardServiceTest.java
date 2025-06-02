package kz.pryakhin.bankrest.service;

import kz.pryakhin.bankrest.dto.card.CardChangeDto;
import kz.pryakhin.bankrest.dto.card.CardDto;
import kz.pryakhin.bankrest.entity.Card;
import kz.pryakhin.bankrest.entity.CardStatus;
import kz.pryakhin.bankrest.entity.User;
import kz.pryakhin.bankrest.exception.CardNotFoundException;
import kz.pryakhin.bankrest.mapper.CardMapper;
import kz.pryakhin.bankrest.repository.CardRepository;
import kz.pryakhin.bankrest.util.CardNumberEncoder;
import kz.pryakhin.bankrest.util.CardNumberGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CardServiceTest {

	@Mock
	private CardRepository cardRepository;

	@Mock
	private CardMapper cardMapper;

	@Mock
	private CardNumberEncoder cardNumberEncoder;

	@Mock
	private UserService userService;

	@Mock
	private Principal principal;

	private Card card;
	private CardDto cardDto;
	private User user;

	@InjectMocks
	private CardService cardService;

	private AutoCloseable closeable;


	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		cardService = new CardService(cardRepository, cardMapper, cardNumberEncoder, userService);
		ReflectionTestUtils.setField(cardService, "bin", "400000");
		ReflectionTestUtils.setField(cardService, "length", 16);

		card = new Card();
		card.setId(1L);
		card.setMaskedSuffix("1234");
		card.setStatus(CardStatus.ACTIVE);
		card.setBalance(BigDecimal.TEN);

		cardDto = new CardDto();
		cardDto.setId(1L);
		cardDto.setMaskedNumber("**** **** **** 1234");
		cardDto.setStatus(CardStatus.ACTIVE);
		cardDto.setBalance(BigDecimal.TEN);

		user = new User();
		user.setId(42L);
		user.setEmail("andrey@gmail.com");
	}


	@Test
	void getCardById_shouldReturnCard() {
		when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
		Card found = cardService.getCardOrThrow(1L);
		assertThat(found).isEqualTo(card);
	}


	@Test
	void getCardById_shouldThrowExceptionIfNotFound() {
		when(cardRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThatThrownBy(() -> cardService.getCardOrThrow(999L))
				.isInstanceOf(CardNotFoundException.class);
	}


	@Test
	void getCard_shouldReturnCardDto() {
		when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
		when(cardMapper.toDto(card)).thenReturn(cardDto);

		CardDto result = cardService.getCard(1L);
		assertThat(result).isEqualTo(cardDto);
	}


	@Test
	void createCard_shouldCreateNewCard() {
		CardChangeDto dto = new CardChangeDto();
		dto.setBalance(BigDecimal.valueOf(100));
		dto.setExpirationDate(LocalDateTime.now().plusYears(3));
		dto.setStatus(CardStatus.ACTIVE);

		String cardNumber = "1234567890123456";
		when(cardNumberEncoder.encrypt(cardNumber)).thenReturn("encrypted");
		when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));
		when(cardMapper.toDto(any())).thenReturn(cardDto);

		try (MockedStatic<CardNumberGenerator> mocked = mockStatic(CardNumberGenerator.class)) {
			mocked.when(() -> CardNumberGenerator.generateCardNumber("400000", 16))
					.thenReturn(cardNumber);

			CardDto result = cardService.createCard(dto);

			assertThat(result).isEqualTo(cardDto);
			verify(cardRepository).save(any(Card.class));
		}
	}


	@Test
	void updateCard_shouldUpdateCardFields() {
		when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
		when(cardRepository.save(any(Card.class))).thenReturn(card);
		when(cardMapper.toDto(card)).thenReturn(cardDto);

		CardChangeDto dto = new CardChangeDto();
		dto.setBalance(BigDecimal.valueOf(150));
		dto.setStatus(CardStatus.BLOCKED);
		dto.setExpirationDate(LocalDateTime.now().plusYears(1));

		CardDto result = cardService.updateCard(1L, dto);

		assertThat(result).isEqualTo(cardDto);
		assertThat(card.getBalance()).isEqualTo(dto.getBalance());
		assertThat(card.getStatus()).isEqualTo(dto.getStatus());
	}


	@Test
	void blockCard_shouldChangeStatusToBlocked() {
		card.setStatus(CardStatus.ACTIVE);

		when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
		when(cardRepository.save(any())).thenReturn(card);
		when(cardMapper.toDto(any())).thenAnswer(inv -> {
			Card input = inv.getArgument(0);
			CardDto dto = new CardDto();
			dto.setStatus(input.getStatus());
			return dto;
		});

		CardDto result = cardService.blockCard(1L);

		assertThat(result.getStatus()).isEqualTo(CardStatus.BLOCKED);
	}


	@Test
	void activateCard_shouldChangeStatusToActive() {
		card.setStatus(CardStatus.BLOCKED);
		when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
		when(cardRepository.save(any())).thenReturn(card);
		when(cardMapper.toDto(card)).thenReturn(cardDto);

		CardDto result = cardService.activateCard(1L);

		assertThat(result.getStatus()).isEqualTo(CardStatus.ACTIVE);
	}


	@Test
	void blockMyCardRequest_shouldMarkAsRequestedBlock() {
		card.setStatus(CardStatus.ACTIVE);
		card.setOwner(user);

		when(principal.getName()).thenReturn("andrey@gmail.com");
		when(userService.getUserByEmail("andrey@gmail.com")).thenReturn(user);
		when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
		when(cardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(cardMapper.toDto(any())).thenAnswer(inv -> {
			Card c = inv.getArgument(0);
			CardDto dto = new CardDto();
			dto.setStatus(c.getStatus());
			return dto;
		});

		CardDto result = cardService.blockMyCardRequest(1L, principal);

		assertThat(result.getStatus()).isEqualTo(CardStatus.REQUESTED_BLOCK);
	}


	@Test
	void confirmBlockRequest_shouldBlockRequestedCard() {
		card.setStatus(CardStatus.REQUESTED_BLOCK);

		when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
		when(cardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(cardMapper.toDto(any())).thenAnswer(inv -> {
			Card c = inv.getArgument(0);
			CardDto dto = new CardDto();
			dto.setStatus(c.getStatus());
			return dto;
		});

		CardDto result = cardService.confirmBlockRequest(1L);

		assertThat(result.getStatus()).isEqualTo(CardStatus.BLOCKED);
	}


	@Test
	void deleteCard_shouldCallRepositoryDelete() {
		cardService.deleteCard(1L);
		verify(cardRepository).deleteById(1L);
	}


	@Test
	void getCards_shouldSearchBySuffix() {
		Page<Card> page = new PageImpl<>(List.of(card));
		when(cardRepository.findByMaskedSuffixContaining("1234", PageRequest.of(0, 10))).thenReturn(page);
		when(cardMapper.toDto(card)).thenReturn(cardDto);

		List<CardDto> result = cardService.getCards(0, 10, "1234");
		assertThat(result).containsExactly(cardDto);
	}


	@AfterEach
	void tearDown() throws Exception {
		closeable.close();
	}
}
