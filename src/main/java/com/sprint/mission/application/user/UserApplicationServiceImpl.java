package com.sprint.mission.application.user;

import com.sprint.mission.controller.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.controller.dto.user.UserCreateRequest;
import com.sprint.mission.controller.dto.user.UserDto;
import com.sprint.mission.controller.dto.user.UserUpdateRequest;
import com.sprint.mission.controller.dto.userstatus.UserStatusDto;
import com.sprint.mission.controller.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.domain.BinaryContent;
import com.sprint.mission.domain.User;
import com.sprint.mission.domain.UserStatus;
import com.sprint.mission.multipart.MultipartFileConverter;
import com.sprint.mission.multipart.MultipartFileDto;
import com.sprint.mission.service.binarycontent.BinaryContentDomainService;
import com.sprint.mission.service.user.UserDomainService;
import com.sprint.mission.service.userstatus.UserStatusDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class UserApplicationServiceImpl implements UserApplicationService {
    private final UserDomainService userDomainService;
    private final BinaryContentDomainService binaryContentDomainService;
    private final UserStatusDomainService userStatusDomainService;
    private final MultipartFileConverter multipartFileConverter;

    @Override
    public UserDto create(
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

        User createdUser = userDomainService.create(User.create(
                userCreateRequest.getUsername(),
                userCreateRequest.getEmail(),
                userCreateRequest.getPassword(),
                createdProfileImage
        ));

        log.info(
                "User 생성 완료: userId={}, profileId={}",
                createdUser.getId(),
                Objects.isNull(createdUser.getProfile()) ? null : createdUser.getProfile().getId()
        );

        return toDto(createdUser);
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
    @Transactional(readOnly = true)
    public UserDto findById(UUID userId) {
        log.debug("User 단일 조회: userId={}", userId);
        User user = userDomainService.findById(userId);

        return toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<UserDto> userResponses = userDomainService.findAll().stream()
                .map(this::toDto)
                .toList();

        log.debug("User 다건 조회: size={}", userResponses.size());
        return userResponses;
    }

    @Override
    public UserDto update(
            UUID userId,
            UserUpdateRequest userUpdateRequest,
            MultipartFile profileImage
    ) {
        User updatingUser = userDomainService.findById(userId);
        BinaryContent oldProfile = updatingUser.getProfile();

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
                createdProfileImage
        );

        User updatedUser = userDomainService.update(updatingUser);

        if (Objects.nonNull(createdProfileImage) && Objects.nonNull(oldProfile)) {
            binaryContentDomainService.delete(oldProfile.getId());
            log.debug(
                    "기존 프로필 삭제 완료: userId={}, oldProfileId={}",
                    userId,
                    oldProfile.getId()
            );
        }

        log.info(
                "User 수정 완료: userId={}, profileId={}",
                updatedUser.getId(),
                Objects.isNull(updatedUser.getProfile()) ? null : updatedUser.getProfile().getId()
        );

        return toDto(updatedUser);
    }

    @Override
    public UserStatusDto updateUserStatusByUserId(
            UUID userId,
            UserStatusUpdateRequest request
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

        return UserStatusDto.from(updatedUserStatus);
    }

    @Override
    public void delete(UUID userId) {
        User user = userDomainService.findById(userId);
        BinaryContent profile = user.getProfile();
        UUID userStatusId = user.getStatus().getId();

        log.info("User 삭제 시작: userId={}", userId);

        userDomainService.delete(userId);   // UserStatus 는 cascade 로 함께 삭제
        if (Objects.nonNull(profile)) {
            binaryContentDomainService.delete(profile.getId());
        }

        log.info(
                "User 삭제 완료: userId={}, userStatusId={}, profileId={}",
                userId,
                userStatusId,
                Objects.isNull(profile) ? null : profile.getId()
        );
    }

    private UserDto toDto(User user) {
        BinaryContentDto profile = Objects.isNull(user.getProfile())
                ? null
                : BinaryContentDto.from(user.getProfile());

        return UserDto.from(user, profile, user.getStatus());
    }
}
