package com.sprint.mission.application.channel;

import com.sprint.mission.application.user.UserApplicationService;
import com.sprint.mission.controller.dto.channel.ChannelDto;
import com.sprint.mission.controller.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.controller.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.controller.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.domain.Channel;
import com.sprint.mission.domain.ChannelType;
import com.sprint.mission.domain.Message;
import com.sprint.mission.domain.ReadStatus;
import com.sprint.mission.domain.User;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import com.sprint.mission.service.channel.ChannelDomainService;
import com.sprint.mission.service.message.MessageDomainService;
import com.sprint.mission.service.readstatus.ReadStatusDomainService;
import com.sprint.mission.service.user.UserDomainService;
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
public class ChannelApplicationServiceImpl implements ChannelApplicationService {
    private final UserApplicationService userApplicationService;
    private final ChannelDomainService channelDomainService;
    private final ReadStatusDomainService readStatusDomainService;
    private final MessageDomainService messageDomainService;
    private final UserDomainService userDomainService;

    @Override
    public ChannelDto createPublic(PublicChannelCreateRequest request) {
        Channel createdChannel = channelDomainService.create(
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
                .map(userDomainService::findById)
                .toList();

        log.info("PRIVATE Channel 생성 시작: participantCount={}", participants.size());

        Channel createdChannel = channelDomainService.create(Channel.createPrivate());

        participants.forEach(user ->
                readStatusDomainService.create(ReadStatus.create(user, createdChannel))
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
        Channel channel = channelDomainService.findById(channelId);

        Message mostRecentMessage = messageDomainService.findMostRecentByChannelId(channelId);
        Instant mostRecentMessageAt = Objects.nonNull(mostRecentMessage)
                ? mostRecentMessage.getCreatedAt()
                : null;

        List<UUID> participantUserIds = (channel.getType() == ChannelType.PRIVATE)
                ? readStatusDomainService.findAllByChannelId(channelId)
                    .stream()
                    .map(readStatus -> readStatus.getUser().getId())
                    .toList()
                : List.of();

        return new ChannelDto(
                channel.getId(), channel.getType(), channel.getName(), channel.getDescription(),
                participantUserIds.stream()
                        .map(userApplicationService::findById)
                        .toList(),
                mostRecentMessageAt
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllByUserId(UUID userId) {
        userDomainService.findById(userId);

        List<Channel> channels = channelDomainService.findAll();

        Set<UUID> accessibleChannelIds = readStatusDomainService.findAllByUserId(userId).stream()
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
        Channel updatingChannel = channelDomainService.findById(channelId);

        if (updatingChannel.getType().equals(ChannelType.PRIVATE)) {
            log.warn("비공개 Channel 수정 불가: channelId={}", channelId);
            throw new DiscodeitException(DiscodeitExceptionType.PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED, channelId);
        }

        updatingChannel.updateNameAndDescription(request.getNewName(), request.getNewDescription());
        Channel updatedChannel = channelDomainService.update(updatingChannel);

        log.info("Channel 수정 완료: channelId={}", updatedChannel.getId());
        return findById(updatedChannel.getId());
    }

    @Override
    public void delete(UUID channelId) {
        channelDomainService.findById(channelId);

        List<Message> messages = messageDomainService.findAllByChannelId(channelId);
        int attachmentCount = messages.stream()
                .mapToInt(message -> message.getAttachments().size())
                .sum();

        log.info(
                "Channel 삭제 시작: channelId={}, messageCount={}, attachmentCount={}",
                channelId,
                messages.size(),
                attachmentCount
        );

        readStatusDomainService.deleteAllByChannelId(channelId);
        messageDomainService.deleteAllByChannelId(channelId);   // 첨부파일은 cascade 로 함께 삭제
        channelDomainService.delete(channelId);

        log.info("Channel 및 연관 데이터 삭제 완료: channelId={}", channelId);
    }
}
