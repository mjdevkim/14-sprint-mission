package com.sprint.mission.controller.api;

import com.sprint.mission.application.channel.ChannelApplicationService;
import com.sprint.mission.controller.dto.channel.ChannelDto;
import com.sprint.mission.controller.dto.channel.ChannelResponseDto;
import com.sprint.mission.controller.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.controller.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.controller.dto.channel.PublicChannelUpdateRequest;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
@Tag(name = "Channel", description = "Channel API")
/**
 * [X] 공개 채널을 생성할 수 있다.
 * [X] 비공개 채널을 생성할 수 있다.
 * [X] 공개 채널의 정보를 수정할 수 있다.
 * [X] 채널을 삭제할 수 있다.
 * [X] 특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.
 */
public class ChannelApiController {

    private final ChannelApplicationService channelApplicationService;

    @Operation(summary = "Public Channel 생성")
    @ApiResponse(
            responseCode = "201",
            description = "Public Channel이 성공적으로 생성됨",
            content = @Content(
                    mediaType = "*/*",
                    schema = @Schema(implementation = ChannelResponseDto.class)
            )
    )
    @PostMapping("/public")
    public ResponseEntity<ChannelResponseDto> create(
            @Valid @RequestBody PublicChannelCreateRequest request
    ) {
        ChannelResponseDto createdChannel = channelApplicationService.createPublic(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    @Operation(summary = "Private Channel 생성")
    @ApiResponse(
            responseCode = "201",
            description = "Private Channel이 성공적으로 생성됨",
            content = @Content(
                    mediaType = "*/*",
                    schema = @Schema(implementation = ChannelResponseDto.class)
            )
    )
    @PostMapping("/private")
    public ResponseEntity<ChannelResponseDto> create(
            @Valid @RequestBody PrivateChannelCreateRequest request
    ) {
        ChannelResponseDto createdChannel = channelApplicationService.createPrivate(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    @Operation(summary = "Channel 정보 수정")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Channel 정보가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = ChannelResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Private Channel은 수정할 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "Private channel cannot be updated")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "Channel with id {channelId} not found")
                    )
            )
    })
    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelResponseDto> update(
            @Parameter(description = "수정할 Channel ID")
            @NotNull @PathVariable UUID channelId,
            @Valid @RequestBody PublicChannelUpdateRequest request
    ) {
        ChannelResponseDto updatedChannel = channelApplicationService.update(channelId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedChannel);
    }

    @Operation(summary = "Channel 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Channel이 성공적으로 삭제됨"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "Channel with id {channelId} not found")
                    )
            )
    })
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 Channel ID")
            @NotNull @PathVariable UUID channelId
    ) {
        channelApplicationService.delete(channelId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @ApiResponse(
            responseCode = "200",
            description = "Channel 목록 조회 성공",
            content = @Content(
                    mediaType = "*/*",
                    array = @ArraySchema(
                            schema = @Schema(implementation = ChannelDto.class)
                    )
            )
    )
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll(
            @Parameter(description = "조회할 User ID")
            @NotNull @RequestParam UUID userId
    ) {
        List<ChannelDto> channels = channelApplicationService.findAllByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(channels);
    }
}
