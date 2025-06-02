package kz.pryakhin.bankrest.dto.card;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferCreateDto {
	@NotNull
	private Long senderCardId;
	@NotNull
	private Long recipientCardId;

	@NotNull
	@DecimalMin("0.01")
	private BigDecimal amount;

}

