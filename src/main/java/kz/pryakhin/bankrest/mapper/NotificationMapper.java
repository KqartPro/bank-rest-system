package kz.pryakhin.bankrest.mapper;


import kz.pryakhin.bankrest.dto.notification.NotificationResponse;
import kz.pryakhin.bankrest.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
	
	@Mapping(target = "userId", source = "user.id")
	NotificationResponse toDto(Notification notification);

	List<NotificationResponse> toDtoList(List<Notification> notifications);


}
