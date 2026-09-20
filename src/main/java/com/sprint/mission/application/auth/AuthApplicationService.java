package com.sprint.mission.application.auth;

import com.sprint.mission.domain.User;
import com.sprint.mission.controller.dto.auth.LoginRequestDto;
import com.sprint.mission.controller.dto.user.UserResponseDto;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import com.sprint.mission.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
public class AuthApplicationService {
    private final UserRepository userRepository;

    public AuthApplicationService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }


    public UserResponseDto login(
            @NotNull @Valid LoginRequestDto loginRequest
    ) {
        User user = userRepository.findByUsername(loginRequest.getUsername())    // username은 고유하다
                .orElseThrow(() -> new DiscodeitException(
                        DiscodeitExceptionType.LOGIN_USER_NOT_FOUND,
                        loginRequest.getUsername()
                ));

        if (!user.matchesPassword(loginRequest.getPassword())) {
            log.error("로그인 실패. username={}", loginRequest.getUsername());
            throw new DiscodeitException(DiscodeitExceptionType.LOGIN_WRONG_PASSWORD);
        }

        log.info("로그인 완료: username={}", user.getUsername());

        return UserResponseDto.from(user);
    }
}
