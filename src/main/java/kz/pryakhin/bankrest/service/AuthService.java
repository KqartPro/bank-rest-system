package kz.pryakhin.bankrest.service;

import kz.pryakhin.bankrest.dto.jwt.RefreshTokenDto;
import kz.pryakhin.bankrest.dto.jwt.TokensDto;
import kz.pryakhin.bankrest.dto.user.UserCredentialsDto;
import kz.pryakhin.bankrest.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserService userService;
	private final JwtService jwtService;


	public TokensDto login(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
		UserDetails user = userService.getUserByCredentials(userCredentialsDto);
		return jwtService.generateAuthTokens(user);
	}


	public TokensDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
		String refreshToken = refreshTokenDto.getRefreshToken();
		if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
			UserDetails user = userService.loadUserByUsername(jwtService.getEmailFromToken(refreshToken));
			return jwtService.refreshAuthToken(user, refreshToken);
		}
		throw new AuthenticationException("Invalid refresh token");
	}
}
