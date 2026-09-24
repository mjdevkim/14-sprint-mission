package com.sprint.mission.application.message;

import com.sprint.mission.application.user.UserApplicationService;
import com.sprint.mission.controller.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.controller.dto.message.MessageCreateRequest;
import com.sprint.mission.controller.dto.message.MessageDto;
import com.sprint.mission.controller.dto.message.MessageUpdateRequest;
import com.sprint.mission.controller.dto.user.UserDto;
import com.sprint.mission.domain.BinaryContent;
import com.sprint.mission.domain.Channel;
import com.sprint.mission.domain.ChannelType;
import com.sprint.mission.domain.Message;
import com.sprint.mission.domain.User;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import com.sprint.mission.multipart.MultipartFileConverter;
import com.sprint.mission.service.channel.ChannelDomainService;
import com.sprint.mission.service.message.MessageDomainService;
import com.sprint.mission.service.readstatus.ReadStatusDomainService;
import com.sprint.mission.service.user.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class MessageApplicationServiceImpl implements MessageApplicationService {
    private final UserApplicationService userApplicationService;
    private final MessageDomainService messageDomainService;
    private final UserDomainService userDomainService;
    private final ChannelDomainService channelDomainService;
    private final ReadStatusDomainService readStatusDomainService;
    private final MultipartFileConverter multipartFileConverter;

    private List<BinaryContent> createAttachments(
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
                .toList();  // 저장은 Message 의 cascade(PERSIST) 로 함께 처리된다
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

        User sender = userDomainService.findById(messageCreateRequest.getAuthorId());
        Channel channel = channelDomainService.findById(messageCreateRequest.getChannelId());

        // 공개 채널이거나 접근 가능한 비공개 채널
        boolean senderCanAccess = channel.getType() == ChannelType.PUBLIC
                || readStatusDomainService.existsByUserIdAndChannelId(sender.getId(), channel.getId());

        if (!senderCanAccess) {
            log.warn(
                    "PRIVATE Channel Message 생성 거부: userId={}, channelId={}",
                    sender.getId(),
                    channel.getId()
            );
            throw new DiscodeitException(
                    DiscodeitExceptionType.CHANNEL_ACCESS_DENIED,
                    sender.getId(),
                    channel.getId()
            );
        }

        List<BinaryContent> attachmentEntities = createAttachments(attachments);

        Message createdMessage = messageDomainService.create(Message.create(
                messageCreateRequest.getContent(),
                sender,
                channel,
                attachmentEntities
        ));

        log.info(
                "Message 생성 완료: messageId={}, senderId={}, channelId={}, attachmentCount={}",
                createdMessage.getId(),
                sender.getId(),
                channel.getId(),
                createdMessage.getAttachments().size()
        );

        return toDto(createdMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto findById(UUID messageId) {
        log.debug("Message 단건 조회: messageId={}", messageId);
        return toDto(messageDomainService.findById(messageId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        channelDomainService.findById(channelId);

        List<MessageDto> messageResponses = messageDomainService.findAllByChannelId(channelId)
                .stream()
                .map(this::toDto)
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

        return toDto(updatedMessage);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageDomainService.findById(messageId);
        int attachmentCount = message.getAttachments().size();

        log.info(
                "Message 삭제 시작: messageId={}, attachmentCount={}",
                message.getId(),
                attachmentCount
        );

        messageDomainService.delete(messageId);     // 첨부파일은 cascade 로 함께 삭제

        log.info("Message 및 첨부파일 삭제 완료: messageId={}", messageId);
    }

    private MessageDto toDto(Message message) {
        // A deleted author must not prevent reading the remaining message history.
        UserDto author = Objects.isNull(message.getAuthor())
                ? null
                : userApplicationService.findById(message.getAuthor().getId());

        List<BinaryContentDto> attachments = message.getAttachments().stream()
                .map(BinaryContentDto::from)
                .toList();

        return MessageDto.from(message, author, attachments);
    }
}
