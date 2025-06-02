package kz.pryakhin.bankrest.controller;

import jakarta.validation.Valid;
import kz.pryakhin.bankrest.dto.jwt.RefreshTokenDto;
import kz.pryakhin.bankrest.dto.jwt.TokensDto;
import kz.pryakhin.bankrest.dto.user.UserCredentialsDto;
import kz.pryakhin.bankrest.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
	private final AuthService authService;


	@PostMapping("/login")
	public TokensDto login(@Valid @RequestBody UserCredentialsDto userCredentialsDto)
			throws AuthenticationException {
		return authService.login(userCredentialsDto);
	}


	@PostMapping("/refresh")
	public TokensDto refresh(@Valid @RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
		return authService.refreshToken(refreshTokenDto);
	}


}
