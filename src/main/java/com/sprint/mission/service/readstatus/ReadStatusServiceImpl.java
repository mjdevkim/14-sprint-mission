package com.sprint.mission.service.readstatus;

import com.sprint.mission.controller.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.controller.dto.readstatus.ReadStatusDto;
import com.sprint.mission.controller.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.domain.Channel;
import com.sprint.mission.domain.ReadStatus;
import com.sprint.mission.domain.User;
import com.sprint.mission.repository.ChannelRepository;
import com.sprint.mission.repository.ReadStatusRepository;
import com.sprint.mission.repository.UserRepository;
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
public class ReadStatusServiceImpl implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        User user = userRepository.getUser(request.getUserId());
        Channel channel = channelRepository.getChannel(request.getChannelId());

        ReadStatus readStatus = ReadStatus.create(
                user,
                channel,
                request.getLastReadAt()
        );
        ReadStatus createdReadStatus = readStatusRepository.createReadStatus(readStatus);

        return ReadStatusDto.from(createdReadStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        userRepository.getUser(userId);

        List<ReadStatusDto> responses =
                readStatusRepository.findAllByUserId(userId)
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
        ReadStatus updatingReadStatus = readStatusRepository.getReadStatus(readStatusId);
        updatingReadStatus.updateLastReadAt(request.getNewLastReadAt());
        ReadStatus updatedReadStatus = updatingReadStatus;

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
