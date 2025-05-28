package kz.pryakhin.bankrest.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCredentialsDto {
	@NotNull
	@Email
	private String email;

	@NotNull
	@Size(min = 8, max = 20)
	private String password;
}
