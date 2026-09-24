package com.sprint.mission.controller.dto.userstatus;

import com.sprint.mission.domain.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "UserStatusDto")
public class UserStatusDto {
    UUID id;
    UUID userId;
    Instant lastActiveAt;

    public static UserStatusDto from(UserStatus userStatus) {
        return new UserStatusDto(
                userStatus.getId(),
                userStatus.getUser().getId(),
                userStatus.getLastActiveAt()
        );
    }
}
