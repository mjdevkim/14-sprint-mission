package com.sprint.mission.controller.dto.readstatus;

import com.sprint.mission.domain.ReadStatus;
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
@Schema(name = "ReadStatus")
public class ReadStatusResponseDto {
    UUID id;
    Instant createdAt;
    Instant updatedAt;
    UUID userId;
    UUID channelId;
    Instant lastReadAt;

    public static ReadStatusResponseDto from(ReadStatus readStatus) {
        return new ReadStatusResponseDto(
                readStatus.getId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt()
        );
    }
}
