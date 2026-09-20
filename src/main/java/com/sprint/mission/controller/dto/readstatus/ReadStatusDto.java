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
@Schema(name = "ReadStatusDto")
public class ReadStatusDto {
    UUID id;
    UUID userId;
    UUID channelId;
    Instant lastReadAt;

    public static ReadStatusDto from(ReadStatus readStatus) {
        return new ReadStatusDto(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt()
        );
    }
}
