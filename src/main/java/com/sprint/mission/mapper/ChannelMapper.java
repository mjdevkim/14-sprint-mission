package com.sprint.mission.mapper;

import com.sprint.mission.controller.dto.channel.ChannelDto;
import com.sprint.mission.controller.dto.user.UserDto;
import com.sprint.mission.domain.Channel;
import com.sprint.mission.domain.ChannelType;
import com.sprint.mission.domain.Message;
import com.sprint.mission.repository.MessageRepository;
import com.sprint.mission.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;

    public ChannelDto toDto(Channel channel) {
        Instant mostRecentMessageAt = messageRepository
                .findFirstByChannelIdOrderByCreatedAtDesc(channel.getId())
                .map(Message::getCreatedAt)
                .orElse(null);

        List<UserDto> participants = (channel.getType() == ChannelType.PRIVATE)
                ? readStatusRepository.findAllByChannelId(channel.getId())
                    .stream()
                    .map(readStatus -> userMapper.toDto(readStatus.getUser()))
                    .toList()
                : List.of();

        return new ChannelDto(
                channel.getId(), channel.getType(), channel.getName(), channel.getDescription(),
                participants,
                mostRecentMessageAt
        );
    }
}
