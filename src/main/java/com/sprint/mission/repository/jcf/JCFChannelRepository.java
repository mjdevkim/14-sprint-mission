package com.sprint.mission.repository.jcf;

import com.sprint.mission.domain.Channel;
import com.sprint.mission.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> channelMap = new HashMap<>();


    @Override
    public Channel save(Channel channel) {
        this.channelMap.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return Optional.ofNullable(this.channelMap.get(channelId));
    }

    @Override
    public List<Channel> findAll() {
        return this.channelMap.values().stream().toList();
    }

    @Override
    public void delete(UUID channelId) {
        channelMap.remove(channelId);
    }
}
