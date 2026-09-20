package com.sprint.mission.application.readstatus;

import com.sprint.mission.domain.ReadStatus;
import com.sprint.mission.controller.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.controller.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.controller.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.service.channel.ChannelDomainService;
import com.sprint.mission.service.readstatus.ReadStatusDomainService;
import com.sprint.mission.service.user.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ReadStatusApplicationServiceImpl implements ReadStatusApplicationService {

    private final ReadStatusDomainService readStatusDomainService;
    private final UserDomainService userDomainService;
    private final ChannelDomainService channelDomainService;

    @Override
    public ReadStatusResponseDto create(ReadStatusCreateRequest request) {
        userDomainService.findById(request.getUserId());
        channelDomainService.findById(request.getChannelId());

        ReadStatus readStatus = ReadStatus.create(
                request.getUserId(),
                request.getChannelId(),
                request.getLastReadAt()
        );
        ReadStatus createdReadStatus = readStatusDomainService.create(readStatus);

        return ReadStatusResponseDto.from(createdReadStatus);
    }

    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        userDomainService.findById(userId);

        List<ReadStatusResponseDto> responses =
                readStatusDomainService.findAllByUserId(userId)
                        .stream()
                        .map(ReadStatusResponseDto::from)
                        .toList();

        log.info("User ReadStatus 목록 조회 완료: userId={}, count={}", userId, responses.size());

        return responses;
    }

    @Override
    public ReadStatusResponseDto update(
            UUID readStatusId,
            ReadStatusUpdateRequest request
    ) {
        ReadStatus updatingReadStatus = readStatusDomainService.findById(readStatusId);
        updatingReadStatus.updateLastReadAt(request.getNewLastReadAt());
        ReadStatus updatedReadStatus = readStatusDomainService.update(updatingReadStatus);

        log.info(
                "ReadStatus 읽음 시간 갱신: readStatusId={}, userId={}, channelId={}, lastReadAt={}",
                updatedReadStatus.getId(),
                updatedReadStatus.getUserId(),
                updatedReadStatus.getChannelId(),
                updatedReadStatus.getLastReadAt()
        );

        return ReadStatusResponseDto.from(updatedReadStatus);
    }
}
