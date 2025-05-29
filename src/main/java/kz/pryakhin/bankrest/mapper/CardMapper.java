package kz.pryakhin.bankrest.mapper;

import kz.pryakhin.bankrest.dto.card.CardDto;
import kz.pryakhin.bankrest.entity.Card;
import kz.pryakhin.bankrest.util.CardMaskHelper;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {
	
	public CardDto toDto(Card card) {
		CardDto dto = new CardDto();

		dto.setId(card.getId());
		dto.setMaskedNumber(CardMaskHelper.maskCardNumber(card.getMaskedSuffix()));
		dto.setBalance(card.getBalance());
		dto.setExpirationDate(card.getExpirationDate());
		dto.setStatus(card.getStatus());

		if (card.getOwner() != null) {
			dto.setUserId(card.getOwner().getId());
		}
		return dto;
	}


}
