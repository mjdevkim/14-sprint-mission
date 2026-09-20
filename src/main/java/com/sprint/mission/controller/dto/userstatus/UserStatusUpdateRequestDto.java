package com.sprint.mission.controller.dto.userstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Schema(name = "UserStatusUpdateRequest", description = "변경할 User 온라인 상태 정보")
public class UserStatusUpdateRequestDto {
    @NotNull
    Instant newLastActiveAt;
}
