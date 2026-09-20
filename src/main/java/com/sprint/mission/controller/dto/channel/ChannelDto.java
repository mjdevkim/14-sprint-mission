package com.sprint.mission.controller.dto.channel;

import com.sprint.mission.domain.Channel;
import com.sprint.mission.domain.ChannelType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ChannelDto {
    UUID id;
    ChannelType type;
    String name;
    String description;
    List<UUID> participantIds;
    Instant lastMessageAt;

    public static ChannelDto from(
            Channel channel,
            List<UUID> participantIds,
            Instant lastMessageAt
    ) {
        return new ChannelDto(
                channel.getId(),
                channel.getChannelType(),
                channel.getName(),
                channel.getDescription(),
                List.copyOf(participantIds),
                lastMessageAt
        );
    }
}
