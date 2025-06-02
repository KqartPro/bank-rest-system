package kz.pryakhin.bankrest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.pryakhin.bankrest.dto.user.UserCreateDto;
import kz.pryakhin.bankrest.dto.user.UserDto;
import kz.pryakhin.bankrest.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
	@Mock
	private UserService userService;

	private ObjectMapper objectMapper;

	@InjectMocks
	private UserController userController;

	private MockMvc mockMvc;


	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
		objectMapper = new ObjectMapper();
	}


	@Test
	void createUser_shouldReturnCreatedUser() throws Exception {
		UserCreateDto userCreateDto = new UserCreateDto("Andrey", "Pryahin", "andrey@gmail.com", "!Password256", "!Password256");
		UserDto userDto = new UserDto(1L, "Andrey", "Pryahin", "andrey@gmail.com");

		Mockito.when(userService.createUser(any())).thenReturn(userDto);

		mockMvc.perform(post("/api/v1/users/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userCreateDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Andrey"))
				.andExpect(jsonPath("$.surname").value("Pryahin"))
				.andExpect(jsonPath("$.email").value("andrey@gmail.com"));
	}


	@Test
	void getUsers_shouldReturnListOfUsers() throws Exception {
		List<UserDto> users = List.of(
				new UserDto(1L, "Andrey", "Pryahin", "andrey@gmail.com"),
				new UserDto(2L, "Misha", "Pryahin", "misha@gmail.com")
		);
		Mockito.when(userService.getUsers(0, 10, null)).thenReturn(users);

		mockMvc.perform(get("/api/v1/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2));
	}


	@Test
	void getUser_shouldReturnSingleUser() throws Exception {
		UserDto userDto = new UserDto(1L, "Andrey", "Pryahin", "andrey@gmail.com");
		Mockito.when(userService.getUserDtoById(1L)).thenReturn(userDto);

		mockMvc.perform(get("/api/v1/users/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("andrey@gmail.com"));
	}


	@Test
	void deleteUser_shouldReturnNoContent() throws Exception {
		mockMvc.perform(delete("/api/v1/users/1"))
				.andExpect(status().isNoContent());
	}


	@Test
	void getPrincipalUser_shouldReturnCurrentUser() throws Exception {
		UserDto userDto = new UserDto(1L, "Current", "User", "user@gmail.com");
		Mockito.when(userService.getUserDtoByEmail("user@gmail.com")).thenReturn(userDto);

		mockMvc.perform(get("/api/v1/users/principal").principal(() -> "user@gmail.com"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("user@gmail.com"));
	}
}