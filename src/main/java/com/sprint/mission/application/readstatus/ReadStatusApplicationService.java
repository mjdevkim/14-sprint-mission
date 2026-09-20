package com.sprint.mission.application.readstatus;

import com.sprint.mission.controller.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.controller.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.controller.dto.readstatus.ReadStatusUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface ReadStatusApplicationService {
    ReadStatusResponseDto create(@NotNull @Valid ReadStatusCreateRequest request);
    List<ReadStatusResponseDto> findAllByUserId(@NotNull UUID userId);
    ReadStatusResponseDto update(
            @NotNull UUID readStatusId,
            @NotNull @Valid ReadStatusUpdateRequest request
    );
}
