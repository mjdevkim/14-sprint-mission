package com.sprint.mission.mapper;

import com.sprint.mission.controller.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.controller.dto.message.MessageDto;
import com.sprint.mission.controller.dto.user.UserDto;
import com.sprint.mission.domain.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;

    public MessageDto toDto(Message message) {
        UserDto author = userMapper.toDto(message.getAuthor());

        List<BinaryContentDto> attachments = message.getAttachments().stream()
                .map(binaryContentMapper::toDto)
                .toList();

        return MessageDto.from(message, author, attachments);
    }
}
