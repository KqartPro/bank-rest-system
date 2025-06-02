package kz.pryakhin.bankrest.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferDto {
	private Long senderCardId;
	private Long recipientCardId;
	private BigDecimal amount;
	private LocalDateTime timestamp;

}
