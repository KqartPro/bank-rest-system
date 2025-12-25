package kz.pryakhin.bankrest.mapper;


import kz.pryakhin.bankrest.dto.user.UserCreateDto;
import kz.pryakhin.bankrest.dto.user.UserDto;
import kz.pryakhin.bankrest.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "authorities", ignore = true)
	User toEntity(UserCreateDto userCreateDto);

	UserDto toDto(User userEntity);


}
