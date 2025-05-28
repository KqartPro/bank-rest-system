package kz.pryakhin.bankrest.service;

import kz.pryakhin.bankrest.dto.user.UserCreateDto;
import kz.pryakhin.bankrest.dto.user.UserCredentialsDto;
import kz.pryakhin.bankrest.dto.user.UserDto;
import kz.pryakhin.bankrest.dto.user.UserUpdateDto;
import kz.pryakhin.bankrest.entity.Role;
import kz.pryakhin.bankrest.entity.User;
import kz.pryakhin.bankrest.exception.UserNotFoundException;
import kz.pryakhin.bankrest.exception.ValidationException;
import kz.pryakhin.bankrest.mapper.UserMapper;
import kz.pryakhin.bankrest.repository.RoleRepository;
import kz.pryakhin.bankrest.repository.UserRepository;
import kz.pryakhin.bankrest.util.ValidationHelper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;


	public List<UserDto> getUsers(int page, int size, String search) {
		Pageable pageable = PageRequest.of(page, size);

		Page<User> result;
		if (search == null || search.trim().isEmpty()) {
			result = userRepository.findAll(pageable);
		} else {
			result = userRepository.findByEmailContainingIgnoreCase(search.trim(), pageable);
		}

		return result.stream()
				.map(userMapper::toDto)
				.toList();
	}


	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
	}


	public User getUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
	}


	public UserDto getUserDtoById(Long userId) {
		return userMapper.toDto(getUserById(userId));
	}


	public UserDto getUserDtoByEmail(String email) {
		return userMapper.toDto(getUserByEmail(email));
	}


	public UserDto createUser(UserCreateDto userCreateDto) throws BadRequestException {
		if (!ValidationHelper.isEmailValid(userCreateDto.getEmail())) {
			throw new ValidationException("Invalid email");
		}

		if (!ValidationHelper.isNameValid(userCreateDto.getName())) {
			throw new ValidationException("Invalid name");
		}

		if (!ValidationHelper.isNameValid(userCreateDto.getSurname())) {
			throw new ValidationException("Invalid surname");
		}

		if (!ValidationHelper.isPasswordValid(userCreateDto.getPassword())) {
			throw new ValidationException("Invalid password");
		}

		if (userRepository.findByEmail(userCreateDto.getEmail()).isPresent()) {
			throw new BadRequestException("User with this email already exists");
		}
		if (!userCreateDto.getPassword().equals(userCreateDto.getRePassword())) {
			throw new BadRequestException("Passwords do not match");
		}

		User user = userMapper.toEntity(userCreateDto);

		user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));

		Optional<Role> optionalRole = roleRepository.findByName("ROLE_USER");
		optionalRole.ifPresent(role -> user.setRoles(List.of(role)));

		return userMapper.toDto(userRepository.save(user));

	}


	public User getUserByCredentials(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
		Optional<User> optionalUser = userRepository.findByEmail(userCredentialsDto.getEmail());
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			if (passwordEncoder.matches(userCredentialsDto.getPassword(), user.getPassword())) {
				return user;
			}
		}
		throw new AuthenticationException("Email or password is not correct");
	}


	public UserDto updateUser(UserUpdateDto userUpdateDto, Long userId) {
		User user = getUserById(userId);

		if (userUpdateDto.getName() != null) {
			if (!ValidationHelper.isNameValid(userUpdateDto.getName())) {
				throw new ValidationException("Invalid Name");
			}

			user.setName(userUpdateDto.getName());

		}

		if (userUpdateDto.getSurname() != null) {
			if (!ValidationHelper.isNameValid(userUpdateDto.getSurname())) {
				throw new ValidationException("Invalid Surname");
			}

			user.setSurname(userUpdateDto.getSurname());


		}
		return userMapper.toDto(userRepository.save(user));

	}


	public void deleteUser(Long userId) {
		userRepository.deleteById(userId);

	}


	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", email)));
	}


}
