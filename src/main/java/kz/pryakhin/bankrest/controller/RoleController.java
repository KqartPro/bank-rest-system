package kz.pryakhin.bankrest.controller;

import kz.pryakhin.bankrest.dto.role.RoleDto;
import kz.pryakhin.bankrest.entity.RoleNames;
import kz.pryakhin.bankrest.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RoleController {
	private final RoleService roleService;


	@GetMapping("/users/{userId}/roles")
	@PreAuthorize("hasRole('ADMIN')")
	public List<RoleDto> getUserRoles(@PathVariable Long userId) {
		return roleService.getUserRoles(userId);
	}


	@PutMapping("/users/{userId}/roles")
	@PreAuthorize("hasRole('ADMIN')")
	public List<RoleDto> updateUserRoles(@PathVariable Long userId, @RequestBody List<RoleNames> roles) {
		return roleService.updateUserRoles(userId, roles);
	}
}
