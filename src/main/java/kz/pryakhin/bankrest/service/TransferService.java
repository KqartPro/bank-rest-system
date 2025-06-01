package kz.pryakhin.bankrest.service;

import jakarta.transaction.Transactional;
import kz.pryakhin.bankrest.dto.card.TransferCreateDto;
import kz.pryakhin.bankrest.dto.card.TransferDto;
import kz.pryakhin.bankrest.entity.Card;
import kz.pryakhin.bankrest.entity.CardStatus;
import kz.pryakhin.bankrest.entity.Transfer;
import kz.pryakhin.bankrest.entity.User;
import kz.pryakhin.bankrest.mapper.TransferMapper;
import kz.pryakhin.bankrest.repository.CardRepository;
import kz.pryakhin.bankrest.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class TransferService {

	private final UserService userService;
	private final CardRepository cardRepository;
	private final TransferMapper transferMapper;
	private final TransferRepository transferRepository;
	private final CardService cardService;


	@Transactional
	public TransferDto transferBetweenOwnCards(TransferCreateDto transferCreateDto, Principal principal) {
		User user = userService.getUserByEmail(principal.getName());

		Card senderCard = cardService.getCardOrThrow(transferCreateDto.getSenderCardId());
		Card recipientCard = cardService.getCardOrThrow(transferCreateDto.getRecipientCardId());


		if (senderCard.getOwner() == null || recipientCard.getOwner() == null) {
			throw new AccessDeniedException("One or both cards do not have an owner");
		}

		if (!user.getId().equals(senderCard.getOwner().getId()) || !user.getId().equals(recipientCard.getOwner().getId())) {
			throw new AccessDeniedException("You can only transfer between your own cards");
		}

		return transferHelper(senderCard, recipientCard, transferCreateDto.getAmount());
	}


	@Transactional
	public TransferDto transferBetweenCards(TransferCreateDto transferCreateDto) {
		Card senderCard = cardService.getCardOrThrow(transferCreateDto.getSenderCardId());
		Card recipientCard = cardService.getCardOrThrow(transferCreateDto.getRecipientCardId());

		return transferHelper(senderCard, recipientCard, transferCreateDto.getAmount());
	}


	private TransferDto transferHelper(Card senderCard, Card recipientCard, BigDecimal amount) {


		if (!senderCard.getStatus().equals(CardStatus.ACTIVE) || !recipientCard.getStatus().equals(CardStatus.ACTIVE)) {
			throw new IllegalStateException("Both cards must be active");
		}


		if (senderCard.getBalance().compareTo(amount) < 0) {
			throw new IllegalStateException("Insufficient funds");
		}

		senderCard.setBalance(senderCard.getBalance().subtract(amount));
		recipientCard.setBalance(recipientCard.getBalance().add(amount));

		cardRepository.save(senderCard);
		cardRepository.save(recipientCard);

		Transfer transfer = new Transfer();
		transfer.setSender(senderCard);
		transfer.setRecipient(recipientCard);
		transfer.setAmount(amount);

		return transferMapper.toDto(transferRepository.save(transfer));
	}


}
