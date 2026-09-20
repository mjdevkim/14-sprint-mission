package com.sprint.mission.controller.api;

import com.sprint.mission.application.binarycontent.BinaryContentApplicationService;
import com.sprint.mission.controller.dto.binarycontent.BinaryContentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@Tag(name = "BinaryContent", description = "첨부 파일 API")
public class BinaryContentApiController {

    private final BinaryContentApplicationService binaryContentApplicationService;

    @Operation(summary = "첨부 파일 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "첨부 파일 조회 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = BinaryContentResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "첨부 파일을 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(
                                    value = "BinaryContent with id {binaryContentId} not found"
                            )
                    )
            )
    })
    @GetMapping("/{binaryContentId}")
    public BinaryContentResponseDto findById(
            @Parameter(description = "조회할 첨부 파일 ID")
            @NotNull @PathVariable UUID binaryContentId
    ) {
        return binaryContentApplicationService.findById(binaryContentId);
    }

    // Binary content id의 list를 주면 그 id를 가진 binary content id들을 반환한다
    @Operation(summary = "여러 첨부 파일 조회")
    @ApiResponse(
            responseCode = "200",
            description = "첨부 파일 목록 조회 성공",
            content = @Content(
                    mediaType = "*/*",
                    array = @ArraySchema(
                            schema = @Schema(implementation = BinaryContentResponseDto.class)
                    )
            )
    )
    @GetMapping
    public List<BinaryContentResponseDto> findAllByIdIn(
            @Parameter(description = "조회할 첨부 파일 ID 목록")
            @RequestParam List<UUID> binaryContentIds
    ) {
        return binaryContentApplicationService.findAllByIdIn(binaryContentIds);
    }
}
