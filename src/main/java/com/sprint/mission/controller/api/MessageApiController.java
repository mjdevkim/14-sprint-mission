package com.sprint.mission.controller.api;

import com.sprint.mission.application.message.MessageApplicationService;
import com.sprint.mission.controller.dto.message.MessageCreateRequest;
import com.sprint.mission.controller.dto.message.MessageResponseDto;
import com.sprint.mission.controller.dto.message.MessageUpdateRequest;
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
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "Message API")
//메시지 관리
//[X] 메시지를 보낼 수 있다.
//[X] 메시지를 수정할 수 있다.
//[X] 메시지를 삭제할 수 있다.
//[X] 특정 채널의 메시지 목록을 조회할 수 있다.
@Validated
public class MessageApiController {

    private final MessageApplicationService messageApplicationService;

    @Operation(summary = "Message 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Message가 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(
                                    value = "Channel | Author with id {channelId | authorId} not found"
                            )
                    )
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponseDto> create(
            @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @Parameter(description = "Message 첨부 파일들")
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        MessageResponseDto createdMessage = messageApplicationService.create(request, attachments);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdMessage);
    }

    @Operation(summary = "Message 내용 수정")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "Message with id {messageId} not found")
                    )
            )
    })
    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageResponseDto> update(
            @Parameter(description = "수정할 Message ID")
            @NotNull @PathVariable UUID messageId,
            @Valid @RequestBody MessageUpdateRequest request
    ) {
        MessageResponseDto updatedMessage = messageApplicationService.update(messageId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedMessage);
    }

    @Operation(summary = "Message 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Message가 성공적으로 삭제됨"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "Message with id {messageId} not found")
                    )
            )
    })
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 Message ID")
            @NotNull @PathVariable UUID messageId
    ) {
        messageApplicationService.delete(messageId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @Operation(summary = "Channel의 Message 목록 조회")
    @ApiResponse(
            responseCode = "200",
            description = "Message 목록 조회 성공",
            content = @Content(
                    mediaType = "*/*",
                    array = @ArraySchema(
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            )
    )
    @GetMapping
    public ResponseEntity<List<MessageResponseDto>> findAllByChannelId(
            @Parameter(description = "조회할 Channel ID")
            @NotNull @RequestParam UUID channelId
    ) {
        List<MessageResponseDto> channelMessageList = messageApplicationService.findAllByChannelId(channelId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(channelMessageList);
    }
}
