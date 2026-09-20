package com.sprint.mission.application.message;

import com.sprint.mission.application.mapper.MessageDtoMapper;
import com.sprint.mission.controller.dto.message.MessageCreateRequest;
import com.sprint.mission.controller.dto.message.MessageDto;
import com.sprint.mission.controller.dto.message.MessageUpdateRequest;
import com.sprint.mission.domain.BinaryContent;
import com.sprint.mission.domain.Channel;
import com.sprint.mission.domain.ChannelType;
import com.sprint.mission.domain.Message;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import com.sprint.mission.multipart.MultipartFileConverter;
import com.sprint.mission.service.binarycontent.BinaryContentDomainService;
import com.sprint.mission.service.channel.ChannelDomainService;
import com.sprint.mission.service.message.MessageDomainService;
import com.sprint.mission.service.readstatus.ReadStatusDomainService;
import com.sprint.mission.service.user.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MessageApplicationServiceImpl implements MessageApplicationService {

    private final MessageDtoMapper messageDtoMapper;
    private final MessageDomainService messageDomainService;
    private final UserDomainService userDomainService;
    private final ChannelDomainService channelDomainService;
    private final BinaryContentDomainService binaryContentDomainService;
    private final ReadStatusDomainService readStatusDomainService;
    private final MultipartFileConverter multipartFileConverter;


    private List<UUID> createAttachments(
            List<MultipartFile> attachmentFiles
    ) {
        if (Objects.isNull(attachmentFiles) || attachmentFiles.isEmpty()) {
            return List.of();
        }

        return attachmentFiles.stream()
                .filter(Objects::nonNull)
                .map(multipartFileConverter::convert)
                .map(converted -> BinaryContent.create(
                        converted.getFileName(),
                        converted.getContentType(),
                        converted.getBytes()
                ))
                .map(binaryContentDomainService::create)
                .map(BinaryContent::getId)
                .toList();
    }


    @Override
    public MessageDto create(
            MessageCreateRequest messageCreateRequest,
            List<MultipartFile> attachments
    ) {
        int numAttachments = (Objects.isNull(attachments))
                ? 0
                : attachments.size();

        log.info(
                "Message 생성 시작: senderId={}, channelId={}, attachmentCount={}",
                messageCreateRequest.getAuthorId(),
                messageCreateRequest.getChannelId(),
                numAttachments
        );

        UUID senderId = userDomainService.findById(messageCreateRequest.getAuthorId()).getId();
        Channel channel = channelDomainService.findById(messageCreateRequest.getChannelId());
        UUID channelId = channel.getId();
        // 공개 채널이거나 접근 가능한 비공개 채널
        boolean senderCanAccess = channel.getChannelType() == ChannelType.PUBLIC
                || readStatusDomainService.existsByUserIdAndChannelId(senderId, channelId);

        if (!senderCanAccess) {
            log.warn(
                    "PRIVATE Channel Message 생성 거부: userId={}, channelId={}",
                    senderId,
                    channelId
            );
            throw new DiscodeitException(
                    DiscodeitExceptionType.CHANNEL_ACCESS_DENIED,
                    senderId,
                    channelId
            );
        }

        List<UUID> attachmentIds = createAttachments(attachments);

        if (Objects.nonNull(attachmentIds)) {
            log.info(
                    "Message 첨부파일 저장 완료: channelId={}, attachmentCount={}",
                    channelId,
                    attachmentIds.size()
            );
        }

        Message createdMessage = messageDomainService.create(Message.create(
                messageCreateRequest.getContent(),
                senderId,
                channelId,
                attachmentIds
        ));

        log.info(
                "Message 생성 완료: messageId={}, senderId={}, channelId={}, attachmentCount={}",
                createdMessage.getId(),
                createdMessage.getSenderId(),
                createdMessage.getChannelId(),
                createdMessage.getAttachmentIds().size()
        );

        return messageDtoMapper.toDto(createdMessage);
    }

    @Override
    public MessageDto findById(UUID messageId) {
        log.debug("Message 단건 조회: messageId={}", messageId);
        return messageDtoMapper.toDto(messageDomainService.findById(messageId));
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        channelDomainService.findById(channelId);

        List<MessageDto> messageResponses = messageDomainService.findAllByChannelId(channelId)
                .stream()
                .map(messageDtoMapper::toDto)
                .toList();

        log.debug(
                "Channel Message 목록 조회 완료: channelId={}, count={}",
                channelId,
                messageResponses.size()
        );

        return messageResponses;
    }

    @Override
    public MessageDto update(
            UUID messageId,
            MessageUpdateRequest messageUpdateRequest
    ) {
        log.info(
                "Message 수정 시작: messageId={}",
                messageId
        );

        Message updatingMessage = messageDomainService.findById(messageId);
        updatingMessage.updateContent(messageUpdateRequest.getNewContent());
        Message updatedMessage = messageDomainService.update(updatingMessage);

        log.info(
                "Message 수정 완료: messageId={}",
                updatedMessage.getId()
        );

        return messageDtoMapper.toDto(updatedMessage);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageDomainService.findById(messageId);
        List<UUID> attachmentIds = messageDomainService.findById(messageId).getAttachmentIds();

        log.info(
                "Message 삭제 시작: messageId={}, attachmentCount={}",
                message.getId(),
                attachmentIds.size()
        );

        messageDomainService.delete(messageId);

        for (UUID attachmentId : attachmentIds) {
            binaryContentDomainService.delete(attachmentId);
        }

        log.info("Message 및 첨부파일 삭제 완료: messageId={}", messageId);
    }
}
