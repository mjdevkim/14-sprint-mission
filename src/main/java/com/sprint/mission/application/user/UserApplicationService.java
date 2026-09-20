package com.sprint.mission.application.user;

import com.sprint.mission.controller.dto.user.UserCreateRequest;
import com.sprint.mission.controller.dto.user.UserDto;
import com.sprint.mission.controller.dto.user.UserResponseDto;
import com.sprint.mission.controller.dto.user.UserUpdateRequest;
import com.sprint.mission.controller.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.controller.dto.userstatus.UserStatusUpdateRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserApplicationService {
    UserResponseDto create(
            @NotNull @Valid UserCreateRequest userCreateRequest,
            MultipartFile profileImage
    );
    UserResponseDto findById(@NotNull UUID userId);
    List<UserDto> findAll();
    UserResponseDto update(
            @NotNull UUID userId,
            @NotNull @Valid UserUpdateRequest userUpdateRequest,
            MultipartFile profileImage
    );
    UserStatusResponseDto updateUserStatusByUserId(
            @NotNull UUID userId,
            @NotNull @Valid UserStatusUpdateRequestDto request
    );
    void delete(@NotNull UUID userId);
}
