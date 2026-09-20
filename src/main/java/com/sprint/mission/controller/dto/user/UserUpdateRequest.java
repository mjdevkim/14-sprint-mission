package com.sprint.mission.controller.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Schema(description = "수정할 User 정보")
public class UserUpdateRequest {
    @NotBlank
    String newUsername;

    @NotBlank
    @Email
    String newEmail;

    @NotBlank
    String newPassword;
}
