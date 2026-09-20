package com.sprint.mission.controller.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(name = "LoginRequest", description = "로그인 정보")
public class LoginRequestDto {

    @NotBlank
    private final String username;

    @NotBlank
    private final String password;

}
