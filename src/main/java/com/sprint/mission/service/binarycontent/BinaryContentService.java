package com.sprint.mission.service.binarycontent;

import com.sprint.mission.controller.dto.binarycontent.BinaryContentDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto create(@NotNull MultipartFile multipartFile);
    BinaryContentDto findById(@NotNull UUID binaryContentId);
    List<BinaryContentDto> findAllByIdIn(@NotNull List<UUID> binaryContentIds);
    BinaryContentDownload download(@NotNull UUID binaryContentId);
    void delete(@NotNull UUID binaryContentId);
}
