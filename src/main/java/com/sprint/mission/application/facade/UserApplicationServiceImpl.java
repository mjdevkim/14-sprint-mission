package com.sprint.mission.application.user;

import com.sprint.mission.controller.dto.user.UserCreateRequest;
import com.sprint.mission.controller.dto.user.UserDto;
import com.sprint.mission.controller.dto.user.UserResponseDto;
import com.sprint.mission.controller.dto.user.UserUpdateRequest;
import com.sprint.mission.controller.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.controller.dto.userstatus.UserStatusUpdateRequestDto;
import com.sprint.mission.domain.*;
import com.sprint.mission.domain.BinaryContent;
import com.sprint.mission.multipart.MultipartFileConverter;
import com.sprint.mission.multipart.MultipartFileDto;
import com.sprint.mission.service.binarycontent.BinaryContentDomainService;
import com.sprint.mission.service.user.UserDomainService;
import com.sprint.mission.service.userstatus.UserStatusDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserDomainService userDomainService;
    private final BinaryContentDomainService binaryContentDomainService;
    private final UserStatusDomainService userStatusDomainService;
    private final MultipartFileConverter multipartFileConverter;


    @Override
    public UserResponseDto create(
            UserCreateRequest userCreateRequest,
            MultipartFile profileImageRequest
    ) {
        // generate binary content
        BinaryContent createdProfileImage = null;

        if (Objects.nonNull(profileImageRequest)) {
            createdProfileImage = createBinaryContent(profileImageRequest);
            log.info(
                    "새 프로필 저장 완료: newProfileId={}",
                    createdProfileImage.getId()
            );
        }

        log.info(
                "User 생성 시작: username={}, email={}, profileImage={}",
                userCreateRequest.getUsername(),
                userCreateRequest.getEmail(),
                Objects.isNull(createdProfileImage) ? "No Pfp" : createdProfileImage.getId()
        );

        // user 생성
        User createdUser = userDomainService.create(User.create(
                userCreateRequest.getUsername(),
                userCreateRequest.getEmail(),
                userCreateRequest.getPassword(),
                (Objects.nonNull(createdProfileImage)) ? createdProfileImage.getId() : null
        ));

        // user의 user status 생성
        userStatusDomainService.create(UserStatus.create(createdUser.getId()));

        log.info(
                "User 생성 완료: userId={}, profileId={}",
                createdUser.getId(),
                createdUser.getProfileId()
        );

        return UserResponseDto.from(createdUser);
    }

    private BinaryContent createBinaryContent(MultipartFile profileImageRequest) {
        MultipartFileDto converted = multipartFileConverter.convert(profileImageRequest);

        BinaryContent binaryContent = BinaryContent.create(
                converted.getFileName(),
                converted.getContentType(),
                converted.getBytes()
        );

        return binaryContentDomainService.create(binaryContent);
    }

    @Override
    public UserResponseDto findById(UUID userId) {
        log.debug("User 단일 조회: userId={}", userId);

        User user = userDomainService.findById(userId);
        return UserResponseDto.from(user);
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userDomainService.findAll();
        List<UserStatus> userStatuses = userStatusDomainService.findAll();
        List<UserDto> userResponses = toUserResponseDtoList(users, userStatuses);

        log.debug("User 다건 조회: size={}", userResponses.size());

        return userResponses;
    }

    @Override
    public UserResponseDto update(
            UUID userId,
            UserUpdateRequest userUpdateRequest,
            MultipartFile profileImage
    ) {
        User updatingUser = userDomainService.findById(userId);
        UUID oldProfileId = updatingUser.getProfileId();
        UserStatus userStatus = userStatusDomainService.findByUserId(userId);

        log.info(
                "User 수정 시작: userId={}, replaceProfile={}",
                userId,
                Objects.nonNull(profileImage) ? "YES" : "N/A"
        );

        // 프로필 사진 있으면 생성
        BinaryContent createdProfileImage = null;
        if (Objects.nonNull(profileImage)) {
            createdProfileImage = createBinaryContent(profileImage);
            log.info(
                    "새 프로필 저장 완료: userId={}, newProfileId={}",
                    userId,
                    createdProfileImage.getId()
            );
        }

        updatingUser.updateAccountDetails(
                userUpdateRequest.getNewUsername(),
                userUpdateRequest.getNewEmail(),
                userUpdateRequest.getNewPassword(),
                (Objects.isNull(createdProfileImage)
                        ? oldProfileId
                        : createdProfileImage.getId()
                )
        );
        User updatedUser = userDomainService.update(updatingUser);

        if (Objects.nonNull(createdProfileImage) && Objects.nonNull(oldProfileId)) {
            binaryContentDomainService.delete(oldProfileId);
            log.debug(
                    "기존 프로필 삭제 완료: userId={}, oldProfileId={}",
                    userId,
                    oldProfileId
            );
        }

        log.info(
                "User 수정 완료: userId={}, profileId={}",
                updatedUser.getId(),
                updatedUser.getProfileId()
        );

        return UserResponseDto.from(updatedUser);
    }

    @Override
    public UserStatusResponseDto updateUserStatusByUserId(
            UUID userId,
            UserStatusUpdateRequestDto request
    ) {
        log.info("User 업데이트 시작: userId={}", userId);

        userDomainService.findById(userId);
        UserStatus updatingUserStatus = userStatusDomainService.findByUserId(userId);
        updatingUserStatus.updateLastActiveAt(request.getNewLastActiveAt());
        UserStatus updatedUserStatus = userStatusDomainService.update(updatingUserStatus);

        log.info(
                "UserStatus 업데이트 완료: userId={}, lastActiveAt={}",
                userId,
                updatedUserStatus.getLastActiveAt()
        );

        return UserStatusResponseDto.from(updatedUserStatus);
    }

    @Override
    public void delete(UUID userId) {
        User user = userDomainService.findById(userId);
        UserStatus userStatus = userStatusDomainService.findByUserId(userId);

        UUID profileId = user.getProfileId();
        BinaryContent profileImage = (Objects.nonNull(profileId))
                ? binaryContentDomainService.findById(profileId)
                : null;

        log.info("User 삭제 시작: userId={}", userId);

        userStatusDomainService.delete(userStatus.getId());
        userDomainService.delete(userId);

        if (Objects.nonNull(profileImage)) {
            binaryContentDomainService.delete(profileImage.getId());
        }

        log.info(
                "User 삭제 완료: userId={}, userStatusId={}, profileId={}",
                userId,
                userStatus.getId(),
                profileId
        );
    }


    // 사용자와 그 사용자의 status까지 같이 반환
    private List<UserDto> toUserResponseDtoList(
            List<User> users,
            List<UserStatus> userStatuses
    ) {
        // { userId : UserStatus } map
        Map<UUID, UserStatus> userStatusMap = new HashMap<>();
        for (UserStatus userStatus : userStatuses) {
            userStatusMap.put(userStatus.getUserId(), userStatus);
        }

        // construct UserResponseDto
        List<UserDto> userResponses = new ArrayList<>();
        for (User user : users) {
            UserStatus userStatus = userStatusMap.get(user.getId());
            if (Objects.isNull(userStatus)) {
                // 방어적 fail
                log.warn(
                        "UserStatus 누락됨: userId={}, isOnline=false 기본값으로 반환함",
                        user.getId()
                );
            }

            userResponses.add(UserDto.from(user, userStatus));
        }
        return userResponses;
    }
}
