package com.sprint.mission.application.channel;

import com.sprint.mission.domain.*;
import com.sprint.mission.controller.dto.channel.ChannelDto;
import com.sprint.mission.controller.dto.channel.ChannelResponseDto;
import com.sprint.mission.controller.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.controller.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.controller.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.domain.BinaryContent;
import com.sprint.mission.domain.Channel;
import com.sprint.mission.domain.ChannelType;
import com.sprint.mission.domain.Message;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import com.sprint.mission.service.binarycontent.BinaryContentDomainService;
import com.sprint.mission.service.channel.ChannelDomainService;
import com.sprint.mission.service.message.MessageDomainService;
import com.sprint.mission.service.readstatus.ReadStatusDomainService;
import com.sprint.mission.service.user.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ChannelApplicationServiceImpl implements ChannelApplicationService {

    private final ChannelDomainService channelDomainService;
    private final ReadStatusDomainService readStatusDomainService;
    private final MessageDomainService messageDomainService;
    private final UserDomainService userDomainService;
    private final BinaryContentDomainService binaryContentDomainService;


    @Override
    public ChannelResponseDto createPublic(PublicChannelCreateRequest request) {
        Channel createdChannel = channelDomainService.create(
                Channel.createPublic(
                        request.getName(),
                        request.getDescription()
                )
        );

        return ChannelResponseDto.from(createdChannel);
    }

    @Override
    public ChannelResponseDto createPrivate(PrivateChannelCreateRequest request) {
        List<UUID> participantUserIds = request.getParticipantIds()
                .stream()
                .distinct()
                .toList();

        participantUserIds.forEach(userDomainService::findById);

        log.info("PRIVATE Channel 생성 시작: participantCount={}", participantUserIds.size());
        Channel createdChannel = channelDomainService.create(Channel.createPrivate());

        participantUserIds.forEach((userId) -> {
            ReadStatus readStatus = ReadStatus.create(
                    userId,
                    createdChannel.getId()
            );
            readStatusDomainService.create(readStatus);
        });

        log.debug(
                "PRIVATE Channel ReadStatus 생성 완료: channelId={}, participantCount={}",
                createdChannel.getId(),
                participantUserIds.size()
        );

        return ChannelResponseDto.from(createdChannel);
    }

    @Override
    public ChannelDto findById(UUID channelId) {
        log.debug("Channel 단건 조회: channelId={}", channelId);

        Channel channel = channelDomainService.findById(channelId);

        Message mostRecentMessage = messageDomainService.findMostRecentByChannelId(channelId);
        Instant mostRecentMessageAt = Objects.nonNull(mostRecentMessage)
                ? mostRecentMessage.getCreatedAt()
                : null;

        List<UUID> participantUserIds = (channel.getChannelType() == ChannelType.PRIVATE)
                ? readStatusDomainService.findAllByChannelId(channelId)
                    .stream()
                    .map(ReadStatus::getUserId)
                    .toList()
                : List.of();

        return ChannelDto.from(
                channel,
                participantUserIds,
                mostRecentMessageAt
        );
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        userDomainService.findById(userId);

        List<Channel> channels = channelDomainService.findAll();

        Set<UUID> accessibleChannelIds = readStatusDomainService.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .collect(Collectors.toSet());

        channels.stream()
                .filter(channel -> channel.getChannelType() == ChannelType.PUBLIC)
                .map(Channel::getId)
                .forEach(accessibleChannelIds::add);

        List<ChannelDto> channelResponses = channelDomainService.findAll().stream()
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
    public ChannelResponseDto update(
            UUID channelId,
            PublicChannelUpdateRequest request
    ) {
        log.info("Channel 수정 시작: channelId={}", channelId);

        Channel updatingChannel = channelDomainService.findById(channelId);
        if (updatingChannel.getChannelType().equals(ChannelType.PRIVATE)) {
            log.warn("비공개 Channel 수정 불가: channelId={}", channelId);
            throw new DiscodeitException(DiscodeitExceptionType.PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED, channelId);
        }

        updatingChannel.updateNameAndDescription(request.getNewName(), request.getNewDescription());
        Channel updatedChannel = channelDomainService.update(updatingChannel);

        log.info("Channel 수정 완료: channelId={}", updatedChannel.getId());

        return ChannelResponseDto.from(updatedChannel);
    }

    @Override
    public void delete(UUID channelId) {
        channelDomainService.findById(channelId);
        List<UUID> messageIds =
                messageDomainService.findAllByChannelId(channelId)
                        .stream()
                        .map(Message::getId)
                        .toList();
        List<UUID> readStatusIds =
                readStatusDomainService.findAllByChannelId(channelId)
                        .stream()
                        .map(ReadStatus::getId)
                        .toList();

        List<UUID> attachmentIds = messageIds.stream()
                .filter(messageId ->
                        Objects.nonNull(
                                messageDomainService.findById(messageId)
                                        .getAttachmentIds())
                )
                // List<Message<Attachment>> 을 flatMap으로
                .flatMap(messageId ->
                        messageDomainService.findById(messageId)
                                .getAttachmentIds()
                                .stream()
                )
                .map(binaryContentDomainService::findById)
                .map(BinaryContent::getId)
                .toList();

        log.info(
                "Channel 삭제 시작: channelId={}, messageCount={}, attachmentCount={}",
                channelId,
                messageIds.size(),
                attachmentIds.size()
        );

        readStatusIds.forEach(readStatusDomainService::delete);
        messageIds.forEach(messageDomainService::delete);
        attachmentIds.forEach(binaryContentDomainService::delete);
        channelDomainService.delete(channelId);

        log.info("Channel 및 연관 데이터 삭제 완료: channelId={}", channelId);
    }

}
