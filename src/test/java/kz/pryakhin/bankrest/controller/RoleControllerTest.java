package kz.pryakhin.bankrest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.pryakhin.bankrest.dto.role.RoleDto;
import kz.pryakhin.bankrest.entity.RoleNames;
import kz.pryakhin.bankrest.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

	@Mock
	private RoleService roleService;

	private ObjectMapper objectMapper;

	@InjectMocks
	private RoleController roleController;

	private MockMvc mockMvc;


	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();
		objectMapper = new ObjectMapper();
	}


	@Test
	void getUserRoles_shouldReturnRoles() throws Exception {
		List<RoleDto> roles = List.of(
				new RoleDto(1L, RoleNames.ROLE_USER.name()),
				new RoleDto(2L, RoleNames.ROLE_ADMIN.name())
		);

		when(roleService.getUserRoles(1L)).thenReturn(roles);

		mockMvc.perform(get("/api/v1/users/1/roles"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].name").value("ROLE_USER"))
				.andExpect(jsonPath("$[1].name").value("ROLE_ADMIN"));
	}


	@Test
	void updateUserRoles_shouldUpdateRoles() throws Exception {
		List<RoleNames> newRoles = List.of(RoleNames.ROLE_ADMIN);
		List<RoleDto> updatedRoles = List.of(
				new RoleDto(1L, RoleNames.ROLE_USER.name()),
				new RoleDto(2L, RoleNames.ROLE_ADMIN.name())
		);

		when(roleService.updateUserRoles(1L, newRoles)).thenReturn(updatedRoles);

		mockMvc.perform(put("/api/v1/users/1/roles")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newRoles)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].name").value("ROLE_USER"))
				.andExpect(jsonPath("$[1].name").value("ROLE_ADMIN"));
	}
}