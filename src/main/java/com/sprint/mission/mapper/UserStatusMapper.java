package com.sprint.mission.mapper;

import com.sprint.mission.controller.dto.userstatus.UserStatusDto;
import com.sprint.mission.domain.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {

    public UserStatusDto toDto(UserStatus userStatus) {
        return UserStatusDto.from(userStatus);
    }
}
