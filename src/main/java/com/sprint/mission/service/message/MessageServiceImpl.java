package com.sprint.mission.service.message;

import com.sprint.mission.controller.dto.message.MessageCreateRequest;
import com.sprint.mission.controller.dto.message.MessageDto;
import com.sprint.mission.controller.dto.message.MessageUpdateRequest;
import com.sprint.mission.controller.dto.response.PageResponse;
import com.sprint.mission.storage.BinaryContentStorage;
import com.sprint.mission.domain.BinaryContent;
import com.sprint.mission.domain.Channel;
import com.sprint.mission.domain.ChannelType;
import com.sprint.mission.domain.Message;
import com.sprint.mission.domain.User;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import com.sprint.mission.mapper.MessageMapper;
import com.sprint.mission.mapper.PageResponseMapper;
import com.sprint.mission.multipart.MultipartFileConverter;
import com.sprint.mission.multipart.MultipartFileDto;
import com.sprint.mission.repository.ChannelRepository;
import com.sprint.mission.repository.MessageRepository;
import com.sprint.mission.repository.ReadStatusRepository;
import com.sprint.mission.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private static final int PAGE_SIZE = 50;
    // Instant.MAX는 DB의 timestamp 컬럼이 표현 가능한 범위를 벗어나므로,
    // "충분히 미래인 값"으로 대신 이 상수를 cursor 기본값(=첫 페이지)으로 사용한다.
    private static final Instant MAX_INSTANT = Instant.parse("9999-12-31T23:59:59Z");

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MultipartFileConverter multipartFileConverter;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;
    private final BinaryContentStorage binaryContentStorage;

    // Message.attachments는 cascade(PERSIST)로 저장되기 때문에, 저장 전에는
    // BinaryContent의 id를 알 수 없다. 그래서 bytes는 따로 들고 있다가
    // Message가 저장되어 attachments에 id가 생긴 뒤에 순서대로 짝지어서 저장한다.
    private List<MultipartFileDto> convertAttachments(
            List<MultipartFile> attachmentFiles
    ) {
        if (Objects.isNull(attachmentFiles) || attachmentFiles.isEmpty()) {
            return List.of();
        }

        return attachmentFiles.stream()
                .filter(Objects::nonNull)
                .map(multipartFileConverter::convert)
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

        List<MultipartFileDto> convertedAttachments = convertAttachments(attachments);
        List<BinaryContent> attachmentEntities = convertedAttachments.stream()
                .map(converted -> BinaryContent.create(
                        converted.getFileName(),
                        converted.getContentType(),
                        converted.getBytes().length
                ))
                .toList();

        Message createdMessage = messageRepository.save(Message.create(
                messageCreateRequest.getContent(),
                sender,
                channel,
                attachmentEntities
        ));

        // attachmentEntities는 Message의 cascade(PERSIST)로 이제 막 저장되어 id가 생겼다.
        // 순서를 그대로 유지하는 리스트이므로 인덱스로 bytes와 다시 짝지을 수 있다.
        List<BinaryContent> savedAttachments = createdMessage.getAttachments();
        for (int i = 0; i < savedAttachments.size(); i++) {
            binaryContentStorage.put(savedAttachments.get(i).getId(), convertedAttachments.get(i).getBytes());
        }

        log.info(
                "Message 생성 완료: messageId={}, senderId={}, channelId={}, attachmentCount={}",
                createdMessage.getId(),
                sender.getId(),
                channel.getId(),
                createdMessage.getAttachments().size()
        );

        return messageMapper.toDto(createdMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto findById(UUID messageId) {
        log.debug("Message 단건 조회: messageId={}", messageId);
        return messageMapper.toDto(messageRepository.getMessage(messageId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor) {
        channelRepository.getChannel(channelId);

        // 커서 유효한지 검증 - 유효하지 않으면 기본값으로
        Instant effectiveCursor = Objects.requireNonNullElse(cursor, MAX_INSTANT);

        // messageRepository의 쿼리 메서드 사용해서 메세지 읽어오기
        List<Message> fetchedMessages = messageRepository.findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(
                channelId, effectiveCursor, Pageable.ofSize(PAGE_SIZE + 1)
        );

        // 읽어온 메세지들을 MessageDto로 변환
        List<MessageDto> fetchedDtos = fetchedMessages.stream()
                .map(messageMapper::toDto)
                .toList();

        // 메세지 리스트를 PageResponse로 변환
        PageResponse<MessageDto> pageResponse = pageResponseMapper.toCursorResponse(
                fetchedDtos, PAGE_SIZE, MessageDto::getCreatedAt, null
        );

        log.debug(
                "Channel Message 목록 조회 완료: channelId={}, cursor={}, count={}, hasNext={}",
                channelId,
                cursor,
                pageResponse.getContent().size(),
                pageResponse.isHasNext()
        );

        return pageResponse;
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

        return messageMapper.toDto(updatedMessage);
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
}
