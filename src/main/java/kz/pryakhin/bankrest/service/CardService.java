package kz.pryakhin.bankrest.service;

import kz.pryakhin.bankrest.dto.card.CardDto;
import kz.pryakhin.bankrest.entity.Card;
import kz.pryakhin.bankrest.entity.CardStatus;
import kz.pryakhin.bankrest.entity.User;
import kz.pryakhin.bankrest.mapper.CardMapper;
import kz.pryakhin.bankrest.repository.CardRepository;
import kz.pryakhin.bankrest.util.CardNumberEncoder;
import kz.pryakhin.bankrest.util.CardNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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


	// Admin Methods


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
			result = cardRepository.findByEncryptedNumberContaining(search, pageable);
		}

		return result.stream()
				.map(cardMapper::toDto)
				.toList();
	}


	public CardDto getCard(Long id) {
		Card card = getCardById(id);

		return cardMapper.toDto(card);

	}


	public CardDto createCard(Long userId) {
		User user = userService.getUserById(userId);

		Card card = new Card();
		card.setOwner(user);
		card.setEncryptedNumber(cardNumberEncoder.encrypt(CardNumberGenerator.generateCardNumber(bin, length)));

		cardRepository.save(card);

		return cardMapper.toDto(card);
	}


	public CardDto activateCard(Long id) {
		Card card = getCardById(id);

		card.setStatus(CardStatus.ACTIVE);
		return cardMapper.toDto(cardRepository.save(card));
	}


	public CardDto blockCard(Long id) {
		Card card = getCardById(id);

		card.setStatus(CardStatus.BLOCKED);
		return cardMapper.toDto(cardRepository.save(card));
	}


	public void deleteCard(Long id) {
		cardRepository.deleteById(id);
	}


	// User Methods


	public List<CardDto> getMyCards(int page, int size, String search, Principal principal) {
		User user = userService.getUserByEmail(principal.getName());

		Pageable pageable = PageRequest.of(page, size);
		return getUserCardsPaginate(user.getId(), search, pageable);
	}


	public CardDto getMyCard(Long id, Principal principal) {
		User user = userService.getUserByEmail(principal.getName());

		return cardMapper.toDto(cardRepository.findByOwnerIdAndId(user.getId(), id).orElseThrow());
	}


	public BigDecimal getMyCardBalance(Long id, Principal principal) {
		User user = userService.getUserByEmail(principal.getName());

		Card card = cardRepository.findByOwnerIdAndId(user.getId(), id).orElseThrow();
		return card.getBalance();
	}


	public CardDto blockMyCardRequest(Long id) {
	}


	// Private Methods


	private Card getCardById(Long id) {
		return cardRepository.findById(id).orElseThrow();
	}


	private List<CardDto> getUserCardsPaginate(Long ownerId, String search, Pageable pageable) {
		Page<Card> result;

		if (search == null || search.trim().isEmpty()) {
			result = cardRepository.findAll(pageable);
		} else {
			result = cardRepository.findByOwnerIdAndEncryptedNumberContaining(ownerId, search, pageable);
		}

		return result.stream()
				.map(cardMapper::toDto)
				.toList();
	}
}
