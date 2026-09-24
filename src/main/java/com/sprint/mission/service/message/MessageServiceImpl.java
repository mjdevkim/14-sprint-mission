package com.sprint.mission.service.message;

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
import com.sprint.mission.repository.ChannelRepository;
import com.sprint.mission.repository.MessageRepository;
import com.sprint.mission.repository.ReadStatusRepository;
import com.sprint.mission.repository.UserRepository;
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
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
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

        User sender = userRepository.getUser(messageCreateRequest.getAuthorId());
        Channel channel = channelRepository.getChannel(messageCreateRequest.getChannelId());

        // 공개 채널이거나 접근 가능한 비공개 채널
        boolean senderCanAccess = channel.getType() == ChannelType.PUBLIC
                || readStatusRepository.existsByUserIdAndChannelId(sender.getId(), channel.getId());

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

        Message createdMessage = messageRepository.save(Message.create(
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
        return toDto(messageRepository.getMessage(messageId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        channelRepository.getChannel(channelId);

        List<MessageDto> messageResponses = messageRepository.findAllByChannelId(channelId)
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

        Message updatingMessage = messageRepository.getMessage(messageId);
        updatingMessage.updateContent(messageUpdateRequest.getNewContent());
        Message updatedMessage = updatingMessage;

        log.info(
                "Message 수정 완료: messageId={}",
                updatedMessage.getId()
        );

        return toDto(updatedMessage);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.getMessage(messageId);
        int attachmentCount = message.getAttachments().size();

        log.info(
                "Message 삭제 시작: messageId={}, attachmentCount={}",
                message.getId(),
                attachmentCount
        );

        messageRepository.deleteById(messageId);

        log.info("Message 및 첨부파일 삭제 완료: messageId={}", messageId);
    }

    private MessageDto toDto(Message message) {
        UserDto author = Objects.isNull(message.getAuthor())
                ? null
                : toUserDto(message.getAuthor());

        List<BinaryContentDto> attachments = message.getAttachments().stream()
                .map(BinaryContentDto::from)
                .toList();

        return MessageDto.from(message, author, attachments);
    }

    private UserDto toUserDto(User user) {
        BinaryContentDto profile = Objects.isNull(user.getProfile())
                ? null
                : BinaryContentDto.from(user.getProfile());

        return UserDto.from(user, profile, user.getStatus());
    }
}
