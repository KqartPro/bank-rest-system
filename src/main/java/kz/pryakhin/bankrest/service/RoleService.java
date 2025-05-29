package kz.pryakhin.bankrest.service;

import jakarta.transaction.Transactional;
import kz.pryakhin.bankrest.dto.role.RoleDto;
import kz.pryakhin.bankrest.entity.Role;
import kz.pryakhin.bankrest.entity.RoleNames;
import kz.pryakhin.bankrest.entity.User;
import kz.pryakhin.bankrest.repository.RoleRepository;
import kz.pryakhin.bankrest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

	private final UserService userService;
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;


	public List<RoleDto> getUserRoles(Long userId) {
		User user = userService.getUserById(userId);

		return user.getRoles().stream()
				.map(role -> new RoleDto(role.getId(), role.getName()))
				.toList();
	}


	@Transactional
	public List<RoleDto> updateUserRoles(Long userId, List<RoleNames> roleNames) {
		User user = userService.getUserById(userId);

		Set<Role> newRoles = roleNames.stream()
				.map(roleName -> roleRepository.findByName(roleName.name())
						.orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
				.collect(Collectors.toSet());

		user.setRoles(newRoles);
		userRepository.save(user);

		return newRoles.stream()
				.map(role -> new RoleDto(role.getId(), role.getName()))
				.toList();
	}
}
