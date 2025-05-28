package kz.pryakhin.bankrest.dto.jwt;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TokensDto {
	@NotNull
	private String token;
	@NotNull
	private String refreshToken;
}
