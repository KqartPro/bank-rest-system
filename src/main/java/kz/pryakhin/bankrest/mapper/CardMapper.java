package kz.pryakhin.bankrest.mapper;

import kz.pryakhin.bankrest.dto.card.CardDto;
import kz.pryakhin.bankrest.entity.Card;
import kz.pryakhin.bankrest.util.CardMaskHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardMapper {
	private final CardMaskHelper cardMaskHelper;


	public CardDto toDto(Card card) {
		CardDto dto = new CardDto();

		dto.setId(card.getId());
		dto.setMaskedNumber(cardMaskHelper.maskCardNumber(card.getEncryptedNumber()));
		dto.setBalance(card.getBalance());
		dto.setExpirationDate(card.getExpirationDate());
		dto.setStatus(card.getStatus());
		dto.setUserId(card.getOwner().getId());
		return dto;
	}


}
