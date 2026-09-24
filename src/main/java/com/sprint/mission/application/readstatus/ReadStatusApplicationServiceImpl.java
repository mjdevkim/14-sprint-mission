package com.sprint.mission.application.readstatus;

import com.sprint.mission.domain.Channel;
import com.sprint.mission.domain.ReadStatus;
import com.sprint.mission.domain.User;
import com.sprint.mission.controller.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.controller.dto.readstatus.ReadStatusDto;
import com.sprint.mission.controller.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.service.channel.ChannelDomainService;
import com.sprint.mission.service.readstatus.ReadStatusDomainService;
import com.sprint.mission.service.user.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class ReadStatusApplicationServiceImpl implements ReadStatusApplicationService {

    private final ReadStatusDomainService readStatusDomainService;
    private final UserDomainService userDomainService;
    private final ChannelDomainService channelDomainService;

    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        User user = userDomainService.findById(request.getUserId());
        Channel channel = channelDomainService.findById(request.getChannelId());

        ReadStatus readStatus = ReadStatus.create(
                user,
                channel,
                request.getLastReadAt()
        );
        ReadStatus createdReadStatus = readStatusDomainService.create(readStatus);

        return ReadStatusDto.from(createdReadStatus);
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        userDomainService.findById(userId);

        List<ReadStatusDto> responses =
                readStatusDomainService.findAllByUserId(userId)
                        .stream()
                        .map(ReadStatusDto::from)
                        .toList();

        log.info("User ReadStatus 목록 조회 완료: userId={}, count={}", userId, responses.size());

        return responses;
    }

    @Override
    public ReadStatusDto update(
            UUID readStatusId,
            ReadStatusUpdateRequest request
    ) {
        ReadStatus updatingReadStatus = readStatusDomainService.findById(readStatusId);
        updatingReadStatus.updateLastReadAt(request.getNewLastReadAt());
        ReadStatus updatedReadStatus = readStatusDomainService.update(updatingReadStatus);

        log.info(
                "ReadStatus 읽음 시간 갱신: readStatusId={}, userId={}, channelId={}, lastReadAt={}",
                updatedReadStatus.getId(),
                updatedReadStatus.getUser().getId(),
                updatedReadStatus.getChannel().getId(),
                updatedReadStatus.getLastReadAt()
        );

        return ReadStatusDto.from(updatedReadStatus);
    }
}
