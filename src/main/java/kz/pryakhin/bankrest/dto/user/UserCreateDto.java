package kz.pryakhin.bankrest.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDto {
	@NotBlank
	@Size(min = 2, max = 30)
	private String name;
	@NotBlank
	@Size(min = 2, max = 30)
	private String surname;
	@Email
	@NotBlank
	private String email;
	@NotBlank
	@Size(min = 8, max = 20)
	private String password;
	@NotBlank
	private String rePassword;
}
