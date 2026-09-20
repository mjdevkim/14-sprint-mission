package com.sprint.mission.service.channel;

import com.sprint.mission.domain.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelDomainService {
    Channel create(Channel channel);
    Channel findById(UUID channelId);
    List<Channel> findAll();
    Channel update(Channel updatingChannel);
    void delete(UUID channelId);
}
