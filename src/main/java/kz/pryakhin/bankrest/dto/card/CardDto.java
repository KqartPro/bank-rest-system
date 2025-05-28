package kz.pryakhin.bankrest.dto.card;

import kz.pryakhin.bankrest.entity.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CardDto {
	private Long id;
	private String maskedNumber;
	private BigDecimal balance;
	private LocalDateTime expirationDate;
	private CardStatus status;
	private Long userId;
}

