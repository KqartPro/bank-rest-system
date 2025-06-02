package kz.pryakhin.bankrest.service;

import kz.pryakhin.bankrest.dto.role.RoleDto;
import kz.pryakhin.bankrest.entity.Role;
import kz.pryakhin.bankrest.entity.RoleNames;
import kz.pryakhin.bankrest.entity.User;
import kz.pryakhin.bankrest.repository.RoleRepository;
import kz.pryakhin.bankrest.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoleServiceTest {
	@Mock
	private UserService userService;
	@Mock
	private RoleRepository roleRepository;
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private RoleService roleService;
	private AutoCloseable closeable;


	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
	}


	@Test
	void getUserRoles_shouldReturnRoleDtos() {
		User user = new User();
		user.setId(1L);

		Role role1 = new Role(1L, RoleNames.ROLE_USER.name());
		Role role2 = new Role(2L, RoleNames.ROLE_ADMIN.name());

		user.setRoles(Set.of(role1, role2));

		when(userService.getUserById(1L)).thenReturn(user);

		List<RoleDto> roles = roleService.getUserRoles(1L);

		assertThat(roles)
				.hasSize(2)
				.extracting(RoleDto::getName)
				.containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
	}


	@Test
	void updateUserRoles_shouldUpdateAndReturnNewRoles() {
		User user = new User();
		user.setId(1L);

		Role adminRole = new Role(2L, RoleNames.ROLE_ADMIN.name());
		Role userRole = new Role(1L, RoleNames.ROLE_USER.name());

		when(userService.getUserById(1L)).thenReturn(user);
		when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
		when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));

		List<RoleDto> result = roleService.updateUserRoles(1L, List.of(RoleNames.ROLE_ADMIN, RoleNames.ROLE_USER));

		verify(userRepository).save(user);

		assertThat(result)
				.hasSize(2)
				.extracting(RoleDto::getName)
				.containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");

		assertThat(user.getRoles())
				.containsExactlyInAnyOrder(adminRole, userRole);
	}


	@AfterEach
	void tearDown() throws Exception {
		closeable.close();
	}
}