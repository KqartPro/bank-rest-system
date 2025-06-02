package kz.pryakhin.bankrest.service;

import kz.pryakhin.bankrest.dto.jwt.RefreshTokenDto;
import kz.pryakhin.bankrest.dto.jwt.TokensDto;
import kz.pryakhin.bankrest.dto.user.UserCredentialsDto;
import kz.pryakhin.bankrest.entity.User;
import kz.pryakhin.bankrest.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import javax.naming.AuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthServiceTest {
	@Mock
	private UserService userService;
	@Mock
	private JwtService jwtService;

	@InjectMocks
	private AuthService authService;

	private AutoCloseable closeable;


	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}


	@Test
	void login_shouldReturnTokensDto() throws AuthenticationException {
		UserCredentialsDto credentials = new UserCredentialsDto("andrey@gmail.com", "password");
		User user = mock(User.class);
		TokensDto expectedTokens = new TokensDto("access-token", "refresh-token");

		when(userService.getUserByCredentials(credentials)).thenReturn(user);
		when(jwtService.generateAuthTokens(user)).thenReturn(expectedTokens);

		TokensDto result = authService.login(credentials);

		assertThat(result).isEqualTo(expectedTokens);
		verify(userService).getUserByCredentials(credentials);
		verify(jwtService).generateAuthTokens(user);
	}


	@Test
	void refreshToken_shouldReturnNewTokensDto() throws Exception {
		String refreshToken = "valid-refresh-token";
		RefreshTokenDto dto = new RefreshTokenDto(refreshToken);
		UserDetails userDetails = mock(UserDetails.class);
		TokensDto newTokens = new TokensDto("new-access-token", "new-refresh-token");

		when(jwtService.validateJwtToken(refreshToken)).thenReturn(true);
		when(jwtService.getEmailFromToken(refreshToken)).thenReturn("andrey@gmail.com");
		when(userService.loadUserByUsername("andrey@gmail.com")).thenReturn(userDetails);
		when(jwtService.refreshAuthToken(userDetails, refreshToken)).thenReturn(newTokens);

		TokensDto result = authService.refreshToken(dto);

		assertThat(result).isEqualTo(newTokens);
		verify(jwtService).validateJwtToken(refreshToken);
		verify(jwtService).getEmailFromToken(refreshToken);
		verify(userService).loadUserByUsername("andrey@gmail.com");
		verify(jwtService).refreshAuthToken(userDetails, refreshToken);
	}


	@Test
	void refreshToken_shouldThrowException_whenTokenInvalid() {
		RefreshTokenDto dto = new RefreshTokenDto("invalid-token");
		when(jwtService.validateJwtToken("invalid-token")).thenReturn(false);

		assertThatThrownBy(() -> authService.refreshToken(dto))
				.isInstanceOf(AuthenticationException.class)
				.hasMessage("Invalid refresh token");

		verify(jwtService).validateJwtToken("invalid-token");
		verifyNoMoreInteractions(jwtService, userService);
	}


	@AfterEach
	void tearDown() throws Exception {
		closeable.close();
	}
}