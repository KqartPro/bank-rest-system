package kz.pryakhin.bankrest.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {
	@Size(min = 2, max = 30)
	private String name;

	@Size(min = 2, max = 30)
	private String surname;

}
