package com.sprint.mission.controller.dto.readstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Schema(description = "Message 읽음 상태 생성 정보")
public class ReadStatusCreateRequest {

    @NotNull
    UUID userId;

    @NotNull
    UUID channelId;

    @NotNull
    Instant lastReadAt;

}
