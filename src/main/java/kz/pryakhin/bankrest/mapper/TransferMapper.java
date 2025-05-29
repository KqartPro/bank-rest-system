package kz.pryakhin.bankrest.mapper;

import kz.pryakhin.bankrest.dto.card.TransferDto;
import kz.pryakhin.bankrest.entity.Transfer;
import org.springframework.stereotype.Component;

@Component
public class TransferMapper {

	public TransferDto toDto(Transfer transfer) {
		TransferDto dto = new TransferDto();

		dto.setSenderCardId(transfer.getSender().getId());
		dto.setRecipientCardId(transfer.getRecipient().getId());
		dto.setAmount(transfer.getAmount());
		dto.setTimestamp(transfer.getTimestamp());

		return dto;
	}
}
