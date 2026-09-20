package com.sprint.mission.controller.dto.user;

import com.sprint.mission.domain.User;
import com.sprint.mission.domain.UserStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import com.sprint.mission.controller.dto.binarycontent.BinaryContentDto;
@Getter
@RequiredArgsConstructor
@Schema(name = "UserDto")
public class UserDto {
    private final UUID id;
    private final String username;
    private final String email;
    private final BinaryContentDto profile;
    private final boolean online;

    public static UserDto from(User user, BinaryContentDto profile, UserStatus status) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(),
                profile, status != null && status.isOnline());
    }
}
