package com.sprint.mission.service.readstatus;

import com.sprint.mission.controller.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.controller.dto.readstatus.ReadStatusDto;
import com.sprint.mission.controller.dto.readstatus.ReadStatusUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto create(@NotNull @Valid ReadStatusCreateRequest request);
    List<ReadStatusDto> findAllByUserId(@NotNull UUID userId);
    ReadStatusDto update(
            @NotNull UUID readStatusId,
            @NotNull @Valid ReadStatusUpdateRequest request
    );
}
