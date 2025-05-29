package kz.pryakhin.bankrest.service;

import jakarta.transaction.Transactional;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
	private final CardRepository cardRepository;
	private final CardMapper cardMapper;
	private final CardNumberEncoder cardNumberEncoder;
	private final UserService userService;

	@Value("${card.number.bin}")
	private String bin;

	@Value("${card.number.length}")
	private int length;


	// Get


	public List<CardDto> getUserCards(Long ownerId, int page, int size, String search) {
		Pageable pageable = PageRequest.of(page, size);

		return getUserCardsPaginate(ownerId, search, pageable);
	}


	public List<CardDto> getCards(int page, int size, String search) {
		Pageable pageable = PageRequest.of(page, size);

		Page<Card> result;
		if (search == null || search.trim().isEmpty()) {
			result = cardRepository.findAll(pageable);
		} else {
			result = cardRepository.findByMaskedSuffixContaining(search, pageable);
		}

		return result.stream()
				.map(cardMapper::toDto)
				.toList();
	}


	public CardDto getCard(Long id) {
		Card card = getCardById(id);

		return cardMapper.toDto(card);

	}


	public Card getCardOrThrow(Long id) {
		return cardRepository.findById(id)
				.orElseThrow(() -> new CardNotFoundException("Card with ID " + id + " not found"));
	}


	public List<CardDto> getMyCards(int page, int size, String search, Principal principal) {
		User user = userService.getUserByEmail(principal.getName());

		Pageable pageable = PageRequest.of(page, size);
		return getUserCardsPaginate(user.getId(), search, pageable);
	}


	public CardDto getMyCard(Long id, Principal principal) {
		User user = userService.getUserByEmail(principal.getName());

		return cardMapper.toDto(cardRepository.findByOwnerIdAndId(user.getId(), id)
				.orElseThrow(CardNotFoundException::new));
	}


	public BigDecimal getMyCardBalance(Long id, Principal principal) {
		User user = userService.getUserByEmail(principal.getName());

		Card card = cardRepository.findByOwnerIdAndId(user.getId(), id)
				.orElseThrow((CardNotFoundException::new));
		return card.getBalance();
	}


	// Post


	@Transactional
	public CardDto createCard(CardChangeDto cardChangeDto) {
		Card card = new Card();

		String generatedCardNumber = CardNumberGenerator.generateCardNumber(bin, length);
		card.setEncryptedNumber(cardNumberEncoder.encrypt(generatedCardNumber));
		card.setMaskedSuffix(generatedCardNumber.substring(generatedCardNumber.length() - 4));

		updateCardProperties(cardChangeDto, card);

		return cardMapper.toDto(cardRepository.save(card));
	}


	// Put


	@Transactional
	public CardDto updateCard(Long id, CardChangeDto cardChangeDto) {
		Card card = getCardById(id);

		updateCardProperties(cardChangeDto, card);

		return cardMapper.toDto(cardRepository.save(card));
	}


	public CardDto activateCard(Long id) {
		Card card = getCardById(id);

		if (card.getStatus() == CardStatus.ACTIVE) {
			throw new IllegalStateException("Card is already active");
		}

		card.setStatus(CardStatus.ACTIVE);
		return cardMapper.toDto(cardRepository.save(card));
	}


	public CardDto blockCard(Long id) {
		Card card = getCardById(id);

		if (card.getStatus() == CardStatus.BLOCKED) {
			throw new IllegalStateException("Card is already blocked");
		}

		card.setStatus(CardStatus.BLOCKED);
		return cardMapper.toDto(cardRepository.save(card));
	}


	@Transactional
	public CardDto blockMyCardRequest(Long id, Principal principal) {
		User user = userService.getUserByEmail(principal.getName());

		Card card = getCardOrThrow(id);

		if (card.getOwner() == null || !card.getOwner().getId().equals(user.getId())) {
			throw new AccessDeniedException("You can only request blocking your own cards");
		}

		if (card.getStatus() != CardStatus.ACTIVE) {
			throw new IllegalStateException("Only active cards can be requested to be blocked");
		}

		card.setStatus(CardStatus.REQUESTED_BLOCK);
		cardRepository.save(card);

		return cardMapper.toDto(card);
	}


	@Transactional
	public CardDto confirmBlockRequest(Long cardId) {
		Card card = getCardOrThrow(cardId);

		if (card.getStatus() != CardStatus.REQUESTED_BLOCK) {
			throw new IllegalStateException("Card is not in block requested status");
		}

		card.setStatus(CardStatus.BLOCKED);
		cardRepository.save(card);

		return cardMapper.toDto(card);
	}


	// Delete


	public void deleteCard(Long id) {
		cardRepository.deleteById(id);
	}


	// Helpers


	private Card getCardById(Long id) {
		return cardRepository.findById(id).orElseThrow(CardNotFoundException::new);
	}


	private List<CardDto> getUserCardsPaginate(Long ownerId, String search, Pageable pageable) {
		Page<Card> result;

		if (search == null || search.trim().isEmpty()) {
			result = cardRepository.findByOwnerId(ownerId, pageable);
		} else {
			result = cardRepository.findByOwnerIdAndMaskedSuffixContaining(ownerId, search, pageable);
		}

		return result.stream()
				.map(cardMapper::toDto)
				.toList();
	}


	private void updateCardProperties(CardChangeDto cardChangeDto, Card card) {
		if (cardChangeDto.getBalance() != null) {
			if (cardChangeDto.getBalance().compareTo(BigDecimal.ZERO) < 0) {
				throw new IllegalArgumentException("Balance can't be negative");
			}
			card.setBalance(cardChangeDto.getBalance());
		}

		if (cardChangeDto.getExpirationDate() != null) {
			card.setExpirationDate(cardChangeDto.getExpirationDate());
		}

		if (cardChangeDto.getStatus() != null) {
			card.setStatus(cardChangeDto.getStatus());
		}

		if (cardChangeDto.getUserId() != null) {
			User user = userService.getUserById(cardChangeDto.getUserId());
			card.setOwner(user);
		}
	}


}
