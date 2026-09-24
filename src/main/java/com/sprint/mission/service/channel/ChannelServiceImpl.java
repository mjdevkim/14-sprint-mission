package com.sprint.mission.service.channel;

import com.sprint.mission.controller.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.controller.dto.channel.ChannelDto;
import com.sprint.mission.controller.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.controller.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.controller.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.controller.dto.user.UserDto;
import com.sprint.mission.domain.Channel;
import com.sprint.mission.domain.ChannelType;
import com.sprint.mission.domain.Message;
import com.sprint.mission.domain.ReadStatus;
import com.sprint.mission.domain.User;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import com.sprint.mission.repository.ChannelRepository;
import com.sprint.mission.repository.MessageRepository;
import com.sprint.mission.repository.ReadStatusRepository;
import com.sprint.mission.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChannelDto createPublic(PublicChannelCreateRequest request) {
        Channel createdChannel = channelRepository.save(
                Channel.createPublic(
                        request.getName(),
                        request.getDescription()
                )
        );

        return findById(createdChannel.getId());
    }

    @Override
    public ChannelDto createPrivate(PrivateChannelCreateRequest request) {
        List<User> participants = request.getParticipantIds()
                .stream()
                .distinct()
                .map(userRepository::getUser)
                .toList();

        log.info("PRIVATE Channel 생성 시작: participantCount={}", participants.size());

        Channel createdChannel = channelRepository.save(Channel.createPrivate());

        participants.forEach(user ->
                readStatusRepository.createReadStatus(ReadStatus.create(user, createdChannel))
        );

        log.debug(
                "PRIVATE Channel ReadStatus 생성 완료: channelId={}, participantCount={}",
                createdChannel.getId(),
                participants.size()
        );

        return findById(createdChannel.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDto findById(UUID channelId) {
        log.debug("Channel 단건 조회: channelId={}", channelId);
        Channel channel = channelRepository.getChannel(channelId);

        Message mostRecentMessage = messageRepository
                .findFirstByChannelIdOrderByCreatedAtDesc(channelId)
                .orElse(null);
        Instant mostRecentMessageAt = Objects.nonNull(mostRecentMessage)
                ? mostRecentMessage.getCreatedAt()
                : null;

        List<UserDto> participants = (channel.getType() == ChannelType.PRIVATE)
                ? readStatusRepository.findAllByChannelId(channelId)
                    .stream()
                    .map(readStatus -> toUserDto(readStatus.getUser()))
                    .toList()
                : List.of();

        return new ChannelDto(
                channel.getId(), channel.getType(), channel.getName(), channel.getDescription(),
                participants,
                mostRecentMessageAt
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllByUserId(UUID userId) {
        userRepository.getUser(userId);

        List<Channel> channels = channelRepository.findAll();

        Set<UUID> accessibleChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatus -> readStatus.getChannel().getId())
                .collect(Collectors.toSet());

        channels.stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC)
                .map(Channel::getId)
                .forEach(accessibleChannelIds::add);

        List<ChannelDto> channelResponses = channels.stream()
                .filter(channel -> accessibleChannelIds.contains(channel.getId()))
                .map(channel -> this.findById(channel.getId()))
                .toList();

        log.debug(
                "User가 볼 수 있는 Channel 목록 조회 완료: userId={}, count={}",
                userId,
                channelResponses.size()
        );

        return channelResponses;
    }

    // 주의: PUBLIC 채널의 name과 description만 바꿀 수 있다.
    @Override
    public ChannelDto update(
            UUID channelId,
            PublicChannelUpdateRequest request
    ) {
        log.info("Channel 수정 시작: channelId={}", channelId);
        Channel updatingChannel = channelRepository.getChannel(channelId);

        if (updatingChannel.getType().equals(ChannelType.PRIVATE)) {
            log.warn("비공개 Channel 수정 불가: channelId={}", channelId);
            throw new DiscodeitException(DiscodeitExceptionType.PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED, channelId);
        }

        updatingChannel.updateNameAndDescription(request.getNewName(), request.getNewDescription());
        Channel updatedChannel = updatingChannel;

        log.info("Channel 수정 완료: channelId={}", updatedChannel.getId());
        return findById(updatedChannel.getId());
    }

    @Override
    public void delete(UUID channelId) {
        channelRepository.getChannel(channelId);

        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        int attachmentCount = messages.stream()
                .mapToInt(message -> message.getAttachments().size())
                .sum();

        log.info(
                "Channel 삭제 시작: channelId={}, messageCount={}, attachmentCount={}",
                channelId,
                messages.size(),
                attachmentCount
        );

        readStatusRepository.deleteAll(readStatusRepository.findAllByChannelId(channelId));
        messageRepository.deleteAll(messages);
        channelRepository.deleteById(channelId);

        log.info("Channel 및 연관 데이터 삭제 완료: channelId={}", channelId);
    }

    private UserDto toUserDto(User user) {
        BinaryContentDto profile = Objects.isNull(user.getProfile())
                ? null
                : BinaryContentDto.from(user.getProfile());

        return UserDto.from(user, profile, user.getStatus());
    }
}
