package kz.pryakhin.bankrest.dto.jwt;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDto {
	@NotNull
	private String refreshToken;
}
