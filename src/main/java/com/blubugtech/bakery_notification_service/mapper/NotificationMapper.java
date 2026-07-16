package com.blubugtech.bakery_notification_service.mapper;

import com.blubugtech.bakery_notification_service.dto.notification.NotificationResponse;
import com.blubugtech.bakery_notification_service.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {
    
    
    NotificationResponse toResponse(Notification notification);
}
