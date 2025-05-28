package kz.pryakhin.bankrest.mapper;


import kz.pryakhin.bankrest.dto.user.UserCreateDto;
import kz.pryakhin.bankrest.dto.user.UserDto;
import kz.pryakhin.bankrest.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
	User toEntity(UserCreateDto userCreateDto);

	UserDto toDto(User userEntity);


}
