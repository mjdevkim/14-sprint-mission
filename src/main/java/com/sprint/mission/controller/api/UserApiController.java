package com.sprint.mission.controller.api;

import com.sprint.mission.application.user.UserApplicationService;
import com.sprint.mission.controller.dto.user.UserCreateRequest;
import com.sprint.mission.controller.dto.user.UserDto;
import com.sprint.mission.controller.dto.user.UserResponseDto;
import com.sprint.mission.controller.dto.user.UserUpdateRequest;
import com.sprint.mission.controller.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.controller.dto.userstatus.UserStatusUpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
// 사용자 관리
// [x] 사용자를 등록할 수 있다.
// [x] 사용자 정보를 수정할 수 있다.
// [x] 사용자를 삭제할 수 있다.
// [x] 모든 사용자를 조회할 수 있다.
// [x] 사용자의 온라인 상태를 업데이트할 수 있다.
@RequestMapping("/api/users")
@Validated
@Tag(name = "User", description = "User API")
public class UserApiController {

    private final UserApplicationService userApplicationService;

    @Operation(summary = "User 등록")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User가 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = UserResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(
                                    value = "User with email {email} already exists"
                            )
                    )
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> create(
            @Valid @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @Parameter(description = "User 프로필 이미지")
            @RequestPart(value = "profile", required = false) MultipartFile profileImage
    ) {
        UserResponseDto createdUser = userApplicationService.create(userCreateRequest, profileImage);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @Operation(summary = "User 정보 수정")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User 정보가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = UserResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(
                                    value = "user with email {newEmail} already exists"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "User with id {userId} not found")
                    )
            )
    })
    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> update(
            @Parameter(description = "수정할 User ID")
            @NotNull @PathVariable UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @Parameter(description = "수정할 User 프로필 이미지")
            @RequestPart(value = "profile", required = false) MultipartFile profileImage
    ) {
        UserResponseDto updatedUser = userApplicationService.update(
                userId,
                userUpdateRequest,
                profileImage
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUser);
    }

    @Operation(summary = "User 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User가 성공적으로 삭제됨"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "User with id {id} not found")
                    )
            )
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 User ID")
            @NotNull @PathVariable UUID userId
    ) {
        userApplicationService.delete(userId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @Operation(summary = "전체 User 목록 조회")
    @ApiResponse(
            responseCode = "200",
            description = "User 목록 조회 성공",
            content = @Content(
                    mediaType = "*/*",
                    array = @ArraySchema(
                            schema = @Schema(implementation = UserDto.class)
                    )
            )
    )
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> usersList = userApplicationService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usersList);
    }

    @Operation(summary = "User 온라인 상태 업데이트")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User 온라인 상태가 성공적으로 업데이트됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = UserStatusResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 User의 UserStatus를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(
                                    value = "UserStatus with userId {userId} not found"
                            )
                    )
            )
    })
    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatusResponseDto> updateUserStatusByUserId(
            @Parameter(description = "상태를 변경할 User ID")
            @NotNull @PathVariable UUID userId,
            @Valid @RequestBody UserStatusUpdateRequestDto request
    ) {
        UserStatusResponseDto updatedUserStatus =
                userApplicationService.updateUserStatusByUserId(userId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUserStatus);
    }
}
