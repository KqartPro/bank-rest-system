package kz.pryakhin.bankrest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.pryakhin.bankrest.dto.jwt.RefreshTokenDto;
import kz.pryakhin.bankrest.dto.jwt.TokensDto;
import kz.pryakhin.bankrest.dto.user.UserCredentialsDto;
import kz.pryakhin.bankrest.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
	@Mock
	private AuthService authService;

	private ObjectMapper objectMapper;

	@InjectMocks
	private AuthController authController;

	private MockMvc mockMvc;


	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
		objectMapper = new ObjectMapper();
	}


	@Test
	void login_shouldReturnTokensDto() throws Exception {
		UserCredentialsDto userCredentialsDto = new UserCredentialsDto("andrey@gmail.com", "!password256");
		TokensDto tokensDto = new TokensDto("access-token", "refresh-token");

		when(authService.login(userCredentialsDto)).thenReturn(tokensDto);

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userCredentialsDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value("access-token"))
				.andExpect(jsonPath("$.refreshToken").value("refresh-token"));

		verify(authService).login(userCredentialsDto);
	}

	
	@Test
	void refresh_shouldReturnNewTokens() throws Exception {
		RefreshTokenDto refreshTokenDto = new RefreshTokenDto("valid-refresh-token");
		TokensDto tokens = new TokensDto("new-access-token", "new-refresh-token");

		when(authService.refreshToken(refreshTokenDto)).thenReturn(tokens);

		mockMvc.perform(post("/api/v1/auth/refresh")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(refreshTokenDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value("new-access-token"))
				.andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));

		verify(authService).refreshToken(refreshTokenDto);
	}


}
