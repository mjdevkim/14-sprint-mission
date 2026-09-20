package com.sprint.mission.controller.dto.message;

import com.sprint.mission.domain.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Schema(name = "Message")
public class MessageResponseDto {

    UUID id;
    Instant createdAt;
    Instant updatedAt;
    String content;
    UUID channelId;
    UUID authorId;
    List<UUID> attachmentIds;

    public static MessageResponseDto from(Message message) {
        return new MessageResponseDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                message.getSenderId(),
                message.getAttachmentIds()
        );
    }
}
