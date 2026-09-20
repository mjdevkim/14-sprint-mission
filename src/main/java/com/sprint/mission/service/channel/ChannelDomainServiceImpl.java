package com.sprint.mission.service.channel;

import com.sprint.mission.domain.Channel;
import com.sprint.mission.domain.ChannelType;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import com.sprint.mission.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Qualifier("channelService")
public class ChannelDomainServiceImpl implements ChannelDomainService {

    private final ChannelRepository channelRepository;

    public ChannelDomainServiceImpl(
            ChannelRepository channelRepository
    ) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel create(Channel channel) {
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel findById(UUID channelId) {
        if (Objects.isNull(channelId)) {
            throw new DiscodeitException(DiscodeitExceptionType.CHANNEL_ID_IS_NULL);
        }

        return channelRepository.findById(channelId)
                .orElseThrow(() -> new DiscodeitException(DiscodeitExceptionType.CHANNEL_NOT_FOUND, channelId));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(Channel updatingChannel) {
        Channel originalChannel = findById(updatingChannel.getId());

        if (originalChannel.getChannelType() == ChannelType.PRIVATE) {
            throw new DiscodeitException(
                    DiscodeitExceptionType.PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED,
                    updatingChannel.getId()
            );
        }

        return channelRepository.save(updatingChannel);
    }

    @Override
    public void delete(UUID channelId) {
        findById(channelId);    // 없으면 CHANNEL_NOT_FOUND
        channelRepository.delete(channelId);
    }
}
