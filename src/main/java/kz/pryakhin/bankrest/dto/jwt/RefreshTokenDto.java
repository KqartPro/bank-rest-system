package kz.pryakhin.bankrest.dto.jwt;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class RefreshTokenDto {
	@NotNull
	private String refreshToken;
}
