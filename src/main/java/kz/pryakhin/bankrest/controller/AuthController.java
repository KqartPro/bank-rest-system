package kz.pryakhin.bankrest.controller;

import jakarta.validation.Valid;
import kz.pryakhin.bankrest.dto.jwt.RefreshTokenDto;
import kz.pryakhin.bankrest.dto.jwt.TokensDto;
import kz.pryakhin.bankrest.dto.user.UserCredentialsDto;
import kz.pryakhin.bankrest.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController extends ApiController {
	private final AuthService authService;


	@PostMapping("/login")
	public ResponseEntity<TokensDto> login(@Valid @RequestBody UserCredentialsDto userCredentialsDto) {
		try {
			TokensDto tokensDto = authService.login(userCredentialsDto);
			return ResponseEntity.ok(tokensDto);

		} catch (AuthenticationException e) {
			throw new RuntimeException("Authentication failed" + e.getMessage());
		}
	}


	@PostMapping("/refresh")
	public TokensDto refresh(@Valid @RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
		return authService.refreshToken(refreshTokenDto);
	}


}
