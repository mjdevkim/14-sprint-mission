package com.sprint.mission.controller.api;

import com.sprint.mission.application.readstatus.ReadStatusApplicationService;
import com.sprint.mission.controller.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.controller.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.controller.dto.readstatus.ReadStatusUpdateRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
@Validated
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
// 메시지 수신 정보 관리
// [X] 채널 생성 시 메시지 수신 정보를 함께 생성할 수 있다.
// [X] 특정 사용자의 특정 채널 메시지 수신 정보를 수정할 수 있다.
// [X] 특정 사용자의 메시지 수신 정보를 조회할 수 있다.
public class ReadStatusApiController {

    private final ReadStatusApplicationService readStatusApplicationService;

    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Message 읽음 상태가 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = ReadStatusResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 읽음 상태가 존재함",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(
                                    value = "ReadStatus with userId {userId} and channelId {channelId} already exists"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(
                                    value = "Channel | User with id {channelId | userId} not found"
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ReadStatusResponseDto> create(
            @Valid @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatusResponseDto createdReadStatus = readStatusApplicationService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdReadStatus);
    }

    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @ApiResponse(
            responseCode = "200",
            description = "Message 읽음 상태 목록 조회 성공",
            content = @Content(
                    mediaType = "*/*",
                    array = @ArraySchema(
                            schema = @Schema(implementation = ReadStatusResponseDto.class)
                    )
            )
    )
    @GetMapping
    public ResponseEntity<List<ReadStatusResponseDto>> findAllByUserId(
            @Parameter(description = "조회할 User ID")
            @NotNull @RequestParam UUID userId
    ) {
        List<ReadStatusResponseDto> readStatusListByUserId = readStatusApplicationService.findAllByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(readStatusListByUserId);
    }

    @Operation(summary = "Message 읽음 상태 수정")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 읽음 상태가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = ReadStatusResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message 읽음 상태를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(
                                    value = "ReadStatus with id {readStatusId} not found"
                            )
                    )
            )
    })
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusResponseDto> update(
            @Parameter(description = "수정할 읽음 상태 ID")
            @NotNull @PathVariable UUID readStatusId,
            @Valid @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatusResponseDto updatedReadStatus = readStatusApplicationService.update(readStatusId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedReadStatus);
    }
}
