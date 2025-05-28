package kz.pryakhin.bankrest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import kz.pryakhin.bankrest.dto.user.UserCreateDto;
import kz.pryakhin.bankrest.dto.user.UserDto;
import kz.pryakhin.bankrest.dto.user.UserUpdateDto;
import kz.pryakhin.bankrest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController extends ApiController {
	private final UserService userService;


	@GetMapping("/principal")
	public UserDto getPrincipalUser(Principal principal) {
		return userService.getUserDtoByEmail(principal.getName());
	}


	@PostMapping("/register")
	public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto) throws BadRequestException {
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(userService.createUser(userCreateDto));
	}


	// Admin


	@GetMapping()
	@PreAuthorize("hasRole('ADMIN')")
	public List<UserDto> getUsers(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Size(min = 1, max = 100) int size,
			@RequestParam(required = false) String search
	) {
		return userService.getUsers(page, size, search);
	}


	@GetMapping("/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public UserDto getUser(@PathVariable Long userId) {
		return userService.getUserDtoById(userId);
	}


	@PatchMapping("/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public UserDto updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto, @PathVariable Long userId) {
		return userService.updateUser(userUpdateDto, userId);
	}


	@DeleteMapping({"/{userId}"})
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
		userService.deleteUser(userId);
		return ResponseEntity.noContent().build();
	}


}
