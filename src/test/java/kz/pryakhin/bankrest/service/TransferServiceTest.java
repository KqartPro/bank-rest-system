package kz.pryakhin.bankrest.service;

import kz.pryakhin.bankrest.dto.card.TransferCreateDto;
import kz.pryakhin.bankrest.dto.card.TransferDto;
import kz.pryakhin.bankrest.entity.Card;
import kz.pryakhin.bankrest.entity.CardStatus;
import kz.pryakhin.bankrest.entity.Transfer;
import kz.pryakhin.bankrest.entity.User;
import kz.pryakhin.bankrest.mapper.TransferMapper;
import kz.pryakhin.bankrest.repository.CardRepository;
import kz.pryakhin.bankrest.repository.TransferRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TransferServiceTest {

	@Mock
	private UserService userService;
	@Mock
	private CardRepository cardRepository;
	@Mock
	private TransferMapper transferMapper;
	@Mock
	private TransferRepository transferRepository;
	@Mock
	private CardService cardService;

	@InjectMocks
	private TransferService transferService;

	private AutoCloseable closeable;


	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}


	@AfterEach
	void tearDown() throws Exception {
		closeable.close();
	}


	@Test
	void transferBetweenOwnCards_shouldSucceed() {
		User user = new User();
		user.setId(1L);
		Principal principal = () -> "user@example.com";

		Card sender = new Card(1L, "1111", BigDecimal.valueOf(1000), CardStatus.ACTIVE, user);
		Card recipient = new Card(2L, "2222", BigDecimal.valueOf(500), CardStatus.ACTIVE, user);
		TransferCreateDto dto = new TransferCreateDto(1L, 2L, BigDecimal.valueOf(300));

		Transfer transfer = new Transfer();
		transfer.setSender(sender);
		transfer.setRecipient(recipient);
		transfer.setAmount(dto.getAmount());

		TransferDto transferDto = new TransferDto();

		when(userService.getUserByEmail("user@example.com")).thenReturn(user);
		when(cardService.getCardOrThrow(1L)).thenReturn(sender);
		when(cardService.getCardOrThrow(2L)).thenReturn(recipient);
		when(transferRepository.save(any(Transfer.class))).thenReturn(transfer);
		when(transferMapper.toDto(any(Transfer.class))).thenReturn(transferDto);

		TransferDto result = transferService.transferBetweenOwnCards(dto, principal);

		assertThat(result).isEqualTo(transferDto);
		assertThat(sender.getBalance()).isEqualByComparingTo("700");
		assertThat(recipient.getBalance()).isEqualByComparingTo("800");

		verify(cardRepository).save(sender);
		verify(cardRepository).save(recipient);
		verify(transferRepository).save(any(Transfer.class));
	}


	@Test
	void transferBetweenCards_shouldSucceed() {
		Card sender = new Card(1L, "1111", BigDecimal.valueOf(1000), CardStatus.ACTIVE, new User());
		Card recipient = new Card(2L, "2222", BigDecimal.valueOf(200), CardStatus.ACTIVE, new User());
		TransferCreateDto dto = new TransferCreateDto(1L, 2L, BigDecimal.valueOf(400));
		Transfer transfer = new Transfer();
		TransferDto transferDto = new TransferDto();

		when(cardService.getCardOrThrow(1L)).thenReturn(sender);
		when(cardService.getCardOrThrow(2L)).thenReturn(recipient);
		when(transferRepository.save(any(Transfer.class))).thenReturn(transfer);
		when(transferMapper.toDto(transfer)).thenReturn(transferDto);

		TransferDto result = transferService.transferBetweenCards(dto);

		assertThat(result).isEqualTo(transferDto);
		assertThat(sender.getBalance()).isEqualByComparingTo("600");
		assertThat(recipient.getBalance()).isEqualByComparingTo("600");

		verify(cardRepository).save(sender);
		verify(cardRepository).save(recipient);
	}


	@Test
	void transferBetweenOwnCards_shouldFail_whenCardsNotOwnedByUser() {
		User user = new User();
		user.setId(1L);
		User otherUser = new User();
		otherUser.setId(2L);
		Principal principal = () -> "user@example.com";

		Card sender = new Card(1L, "1111", BigDecimal.valueOf(1000), CardStatus.ACTIVE, otherUser);
		Card recipient = new Card(2L, "2222", BigDecimal.valueOf(500), CardStatus.ACTIVE, user);
		TransferCreateDto dto = new TransferCreateDto(1L, 2L, BigDecimal.valueOf(300));

		when(userService.getUserByEmail("user@example.com")).thenReturn(user);
		when(cardService.getCardOrThrow(1L)).thenReturn(sender);
		when(cardService.getCardOrThrow(2L)).thenReturn(recipient);

		assertThatThrownBy(() -> transferService.transferBetweenOwnCards(dto, principal))
				.isInstanceOf(org.springframework.security.access.AccessDeniedException.class)
				.hasMessage("You can only transfer between your own cards");
	}


	@Test
	void transferHelper_shouldThrow_whenCardsNotActive() {
		Card sender = new Card(1L, "1111", BigDecimal.valueOf(1000), CardStatus.BLOCKED, new User());
		Card recipient = new Card(2L, "2222", BigDecimal.valueOf(500), CardStatus.ACTIVE, new User());


		when(cardService.getCardOrThrow(1L)).thenReturn(sender);
		when(cardService.getCardOrThrow(2L)).thenReturn(recipient);

		TransferCreateDto dto = new TransferCreateDto(1L, 2L, BigDecimal.valueOf(100));

		assertThatThrownBy(() ->
				transferService.transferBetweenCards(dto)
		).isInstanceOf(IllegalStateException.class)
				.hasMessage("Both cards must be active");
	}


	@Test
	void transferHelper_shouldThrow_whenInsufficientFunds() {
		Card sender = new Card(1L, "1111", BigDecimal.valueOf(100), CardStatus.ACTIVE, new User());
		Card recipient = new Card(2L, "2222", BigDecimal.valueOf(500), CardStatus.ACTIVE, new User());

		when(cardService.getCardOrThrow(1L)).thenReturn(sender);
		when(cardService.getCardOrThrow(2L)).thenReturn(recipient);

		assertThatThrownBy(() -> transferService.transferBetweenCards(
				new TransferCreateDto(1L, 2L, BigDecimal.valueOf(200)))
		).isInstanceOf(IllegalStateException.class)
				.hasMessage("Insufficient funds");
	}
}