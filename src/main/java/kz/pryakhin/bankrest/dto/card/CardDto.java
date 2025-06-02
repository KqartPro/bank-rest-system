package kz.pryakhin.bankrest.dto.card;

import kz.pryakhin.bankrest.entity.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
	private Long id;
	private String maskedNumber;
	private BigDecimal balance;
	private LocalDateTime expirationDate;
	private CardStatus status;
	private Long ownerId;


	public CardDto(Long id, String maskedNumber, BigDecimal balance) {
		this.id = id;
		this.maskedNumber = maskedNumber;
		this.balance = balance;
	}
}
