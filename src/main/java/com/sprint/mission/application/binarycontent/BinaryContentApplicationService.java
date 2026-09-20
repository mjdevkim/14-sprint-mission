package com.sprint.mission.application.binarycontent;

import com.sprint.mission.controller.dto.binarycontent.BinaryContentResponseDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface BinaryContentApplicationService {
    BinaryContentResponseDto create(@NotNull MultipartFile multipartFile);
    BinaryContentResponseDto findById(@NotNull UUID binaryContentId);
    List<BinaryContentResponseDto> findAllByIdIn(@NotNull List<UUID> binaryContentIds);
    void delete(@NotNull UUID binaryContentId);
}
