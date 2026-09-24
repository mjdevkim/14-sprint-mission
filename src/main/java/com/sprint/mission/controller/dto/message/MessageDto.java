package com.sprint.mission.controller.dto.message;

import com.sprint.mission.domain.Message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.Instant;
import java.util.UUID;
import java.util.List;
import com.sprint.mission.controller.dto.user.UserDto;
import com.sprint.mission.controller.dto.binarycontent.BinaryContentDto;
@Getter
@RequiredArgsConstructor
@Schema(name = "MessageDto")
public class MessageDto {
    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String content;
    private final UUID channelId;
    private final UserDto author;
    private final List<BinaryContentDto> attachments;

    public static MessageDto from(Message message, UserDto author, List<BinaryContentDto> attachments) {
        return new MessageDto(message.getId(), message.getCreatedAt(), message.getUpdatedAt(),
                message.getContent(), message.getChannel().getId(), author, List.copyOf(attachments));
    }
}
