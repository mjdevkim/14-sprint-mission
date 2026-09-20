package com.sprint.mission.controller.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Schema(description = "수정할 Channel 정보")
public class PublicChannelUpdateRequest {

    @NotBlank
    String newName;

    @NotBlank
    String newDescription;

}
