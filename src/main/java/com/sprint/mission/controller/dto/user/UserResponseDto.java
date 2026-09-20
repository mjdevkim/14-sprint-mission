package com.sprint.mission.controller.dto.user;

import com.sprint.mission.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Schema(name = "User")
public class UserResponseDto {
    UUID id;
    Instant createdAt;
    Instant updatedAt;
    String username;
    String email;
    String password;
    UUID profileId;

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getProfileId()
        );
    }
}
