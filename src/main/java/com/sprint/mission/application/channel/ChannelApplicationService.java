package com.sprint.mission.application.channel;

import com.sprint.mission.controller.dto.channel.ChannelDto;
import com.sprint.mission.controller.dto.channel.ChannelResponseDto;
import com.sprint.mission.controller.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.controller.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.controller.dto.channel.PublicChannelUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface ChannelApplicationService {
    ChannelResponseDto createPublic(@NotNull @Valid PublicChannelCreateRequest publicChannelCreateRequest);
    ChannelResponseDto createPrivate(@NotNull @Valid PrivateChannelCreateRequest privateChannelCreateRequest);
    ChannelDto findById(@NotNull UUID channelId);
    List<ChannelDto> findAllByUserId(@NotNull UUID userId);
    ChannelResponseDto update(
            @NotNull UUID channelId,
            @NotNull @Valid PublicChannelUpdateRequest channelUpdateRequest
    );
    void delete(@NotNull UUID channelId);
}
