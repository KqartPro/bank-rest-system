package kz.pryakhin.bankrest.service;

import kz.pryakhin.bankrest.dto.user.UserCreateDto;
import kz.pryakhin.bankrest.dto.user.UserDto;
import kz.pryakhin.bankrest.dto.user.UserUpdateDto;
import kz.pryakhin.bankrest.entity.Card;
import kz.pryakhin.bankrest.entity.CardStatus;
import kz.pryakhin.bankrest.entity.Role;
import kz.pryakhin.bankrest.entity.User;
import kz.pryakhin.bankrest.exception.UserNotFoundException;
import kz.pryakhin.bankrest.mapper.UserMapper;
import kz.pryakhin.bankrest.repository.CardRepository;
import kz.pryakhin.bankrest.repository.RoleRepository;
import kz.pryakhin.bankrest.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private RoleRepository roleRepository;
	@Mock
	private CardRepository cardRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private UserMapper userMapper;

	@InjectMocks
	private UserService userService;

	private AutoCloseable closeable;


	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}


	@Test
	void createUser_shouldSaveUserAndReturnDto() throws BadRequestException {
		UserCreateDto dto = new UserCreateDto();
		dto.setEmail("andrey@example.com");
		dto.setName("Andrey");
		dto.setSurname("Pryahin");
		dto.setPassword("!Password123");
		dto.setRePassword("!Password123");

		User user = new User();
		user.setEmail(dto.getEmail());

		UserDto userDto = new UserDto();
		userDto.setEmail(dto.getEmail());

		when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
		when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
		when(userMapper.toEntity(dto)).thenReturn(user);
		when(userRepository.save(any(User.class))).thenReturn(user);
		when(userMapper.toDto(user)).thenReturn(userDto);
		when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(new Role()));

		UserDto result = userService.createUser(dto);

		assertEquals(dto.getEmail(), result.getEmail());
		verify(userRepository).save(any(User.class));
	}


	@Test
	void createUser_shouldThrowIfEmailExists() {
		UserCreateDto dto = new UserCreateDto();
		dto.setEmail("exists@example.com");
		dto.setName("Name");
		dto.setSurname("Surname");
		dto.setPassword("Password1!");
		dto.setRePassword("Password1!");

		when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new User()));

		assertThrows(BadRequestException.class, () -> userService.createUser(dto));
	}


	@Test
	void getUserById_whenNotFound_shouldThrowException() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
	}


	@Test
	void getUsers_withoutSearch_shouldReturnPagedUsers() {
		User user1 = new User();
		user1.setEmail("user1@example.com");
		User user2 = new User();
		user2.setEmail("user2@example.com");

		UserDto userDto1 = new UserDto();
		userDto1.setEmail("user1@example.com");
		UserDto userDto2 = new UserDto();
		userDto2.setEmail("user2@example.com");

		Page<User> page = new PageImpl<>(List.of(user1, user2));
		Pageable pageable = PageRequest.of(0, 2);

		when(userRepository.findAll(pageable)).thenReturn(page);
		when(userMapper.toDto(user1)).thenReturn(userDto1);
		when(userMapper.toDto(user2)).thenReturn(userDto2);

		List<UserDto> result = userService.getUsers(0, 2, null);

		assertEquals(2, result.size());
		assertEquals("user1@example.com", result.get(0).getEmail());
		assertEquals("user2@example.com", result.get(1).getEmail());
	}


	@Test
	void getUsers_withSearch_shouldReturnFilteredUsers() {
		User user = new User();
		user.setEmail("search@example.com");

		UserDto userDto = new UserDto();
		userDto.setEmail("search@example.com");

		Page<User> page = new PageImpl<>(List.of(user));
		Pageable pageable = PageRequest.of(0, 1);

		when(userRepository.findByEmailContainingIgnoreCase("search", pageable)).thenReturn(page);
		when(userMapper.toDto(user)).thenReturn(userDto);

		List<UserDto> result = userService.getUsers(0, 1, "search");

		assertEquals(1, result.size());
		assertEquals("search@example.com", result.getFirst().getEmail());
	}


	@Test
	void updateUser_shouldUpdateNameAndSurname() {
		User existingUser = new User();
		existingUser.setId(1L);
		existingUser.setName("Old");
		existingUser.setSurname("Name");

		UserUpdateDto updateDto = new UserUpdateDto();
		updateDto.setName("New");
		updateDto.setSurname("Surname");

		when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
		when(userRepository.save(any())).thenReturn(existingUser);
		when(userMapper.toDto(existingUser)).thenReturn(new UserDto());

		UserDto result = userService.updateUser(updateDto, 1L);

		assertNotNull(result);
		assertEquals("New", existingUser.getName());
		assertEquals("Surname", existingUser.getSurname());
	}


	@Test
	void deleteUser_shouldBlockCardsAndRemoveUser() {
		User user = new User();
		user.setId(1L);

		List<Card> cards = List.of(new Card(), new Card());
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(cardRepository.findByOwnerId(1L)).thenReturn(cards);

		userService.deleteUser(1L);

		for (Card card : cards) {
			assertEquals(CardStatus.BLOCKED, card.getStatus());
			assertNull(card.getOwner());
		}

		verify(cardRepository).saveAll(cards);
		verify(userRepository).deleteById(1L);
	}


	@AfterEach
	void tearDown() throws Exception {
		closeable.close();
	}
}
