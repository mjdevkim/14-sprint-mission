package com.sprint.mission.controller.dto.channel;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Schema(description = "Private Channel 생성 정보")
public class PrivateChannelCreateRequest {

    @NotEmpty
    List<@NotNull UUID> participantIds;

}
