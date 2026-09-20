package com.sprint.mission.controller.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.Instant;
import java.util.UUID;
import java.util.List;
import com.sprint.mission.domain.ChannelType;
import com.sprint.mission.controller.dto.user.UserDto;
@Getter
@RequiredArgsConstructor
@Schema(name = "ChannelDto")
public class ChannelDto {
    private final UUID id;
    private final ChannelType type;
    private final String name;
    private final String description;
    private final List<UserDto> participants;
    private final Instant lastMessageAt;

}
