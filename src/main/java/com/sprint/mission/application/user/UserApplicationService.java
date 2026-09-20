package com.sprint.mission.application.user;

import com.sprint.mission.controller.dto.user.UserCreateRequest;
import com.sprint.mission.controller.dto.user.UserDto;
import com.sprint.mission.controller.dto.user.UserUpdateRequest;
import com.sprint.mission.controller.dto.userstatus.UserStatusDto;
import com.sprint.mission.controller.dto.userstatus.UserStatusUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserApplicationService {
    UserDto create(
            @NotNull @Valid UserCreateRequest userCreateRequest,
            MultipartFile profileImage
    );
    UserDto findById(@NotNull UUID userId);
    List<UserDto> findAll();
    UserDto update(
            @NotNull UUID userId,
            @NotNull @Valid UserUpdateRequest userUpdateRequest,
            MultipartFile profileImage
    );
    UserStatusDto updateUserStatusByUserId(
            @NotNull UUID userId,
            @NotNull @Valid UserStatusUpdateRequest request
    );
    void delete(@NotNull UUID userId);
}
