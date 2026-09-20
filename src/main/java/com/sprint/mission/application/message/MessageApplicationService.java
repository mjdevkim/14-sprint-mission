package com.sprint.mission.application.message;

import com.sprint.mission.controller.dto.message.MessageCreateRequest;
import com.sprint.mission.controller.dto.message.MessageResponseDto;
import com.sprint.mission.controller.dto.message.MessageUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageApplicationService {
    MessageResponseDto create(
            @NotNull @Valid MessageCreateRequest messageCreateRequest,
            List<MultipartFile> attachment
    );
    MessageResponseDto findById(@NotNull UUID messageId);
    List<MessageResponseDto> findAllByChannelId(@NotNull UUID channelId);
    MessageResponseDto update(
            @NotNull UUID messageId,
            @NotNull @Valid MessageUpdateRequest messageUpdateRequest
    );
    void delete(@NotNull UUID messageId);
}
