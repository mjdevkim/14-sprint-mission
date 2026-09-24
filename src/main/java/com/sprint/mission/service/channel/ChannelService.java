package com.sprint.mission.service.channel;

import com.sprint.mission.controller.dto.channel.ChannelDto;
import com.sprint.mission.controller.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.controller.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.controller.dto.channel.PublicChannelUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto createPublic(@NotNull @Valid PublicChannelCreateRequest publicChannelCreateRequest);
    ChannelDto createPrivate(@NotNull @Valid PrivateChannelCreateRequest privateChannelCreateRequest);
    ChannelDto findById(@NotNull UUID channelId);
    List<ChannelDto> findAllByUserId(@NotNull UUID userId);
    ChannelDto update(
            @NotNull UUID channelId,
            @NotNull @Valid PublicChannelUpdateRequest channelUpdateRequest
    );
    void delete(@NotNull UUID channelId);
}
