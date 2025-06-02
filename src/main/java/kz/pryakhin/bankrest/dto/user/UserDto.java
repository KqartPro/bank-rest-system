package kz.pryakhin.bankrest.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	private Long id;

	@Size(min = 2, max = 30)
	private String name;

	@Size(min = 2, max = 30)
	private String surname;

	@Email
	private String email;

}
