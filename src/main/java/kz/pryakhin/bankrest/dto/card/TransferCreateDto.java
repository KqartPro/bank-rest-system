package kz.pryakhin.bankrest.dto.card;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferCreateDto {
	@NotNull
	private Long senderId;
	@NotNull
	private Long recipientId;

	@NotNull
	@DecimalMin("0.01")
	private BigDecimal amount;

}

