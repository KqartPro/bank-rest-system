package kz.pryakhin.bankrest.dto.card;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferDto {
	private Long senderCardId;
	private Long recipientCardId;
	private BigDecimal amount;
	private LocalDateTime timestamp;

}
