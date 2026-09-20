package com.sprint.mission.controller.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Schema(description = "Message 생성 정보")
public class MessageCreateRequest {

    @NotBlank
    String content;

    @NotNull
    UUID channelId;

    @NotNull
    UUID authorId;

}
