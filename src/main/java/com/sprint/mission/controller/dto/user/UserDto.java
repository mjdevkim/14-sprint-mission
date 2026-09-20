package com.sprint.mission.controller.dto.user;

import com.sprint.mission.domain.User;
import com.sprint.mission.domain.UserStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserDto {

    UUID id;
    Instant createdAt;
    Instant updatedAt;

    String username;
    String email;
    UUID profileId;
    Boolean online;


    // UserDto 통해
    public static UserDto from(
            User user,
            UserStatus userStatus
    ) {
        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                userStatus != null && userStatus.isOnline()
        );
    }
}
