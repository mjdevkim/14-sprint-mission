package com.sprint.mission.service.message;

import com.sprint.mission.controller.dto.message.MessageCreateRequest;
import com.sprint.mission.controller.dto.message.MessageDto;
import com.sprint.mission.controller.dto.message.MessageUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto create(
            @NotNull @Valid MessageCreateRequest messageCreateRequest,
            List<MultipartFile> attachment
    );
    MessageDto findById(@NotNull UUID messageId);
    List<MessageDto> findAllByChannelId(@NotNull UUID channelId);
    MessageDto update(
            @NotNull UUID messageId,
            @NotNull @Valid MessageUpdateRequest messageUpdateRequest
    );
    void delete(@NotNull UUID messageId);
}
