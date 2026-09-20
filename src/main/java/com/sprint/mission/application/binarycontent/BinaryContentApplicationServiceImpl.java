package com.sprint.mission.application.binarycontent;

import com.sprint.mission.domain.BinaryContent;
import com.sprint.mission.controller.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.multipart.MultipartFileConverter;
import com.sprint.mission.multipart.MultipartFileDto;
import com.sprint.mission.service.binarycontent.BinaryContentDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class BinaryContentApplicationServiceImpl implements BinaryContentApplicationService {

    private final BinaryContentDomainService binaryContentDomainService;
    private final MultipartFileConverter multipartFileConverter;

    @Override
    public BinaryContentResponseDto create(MultipartFile multipartFile) {
        MultipartFileDto sanitizedMultipartData = multipartFileConverter.convert(multipartFile);

        BinaryContent binaryContent = BinaryContent.create(
                sanitizedMultipartData.getFileName(),
                sanitizedMultipartData.getContentType(),
                sanitizedMultipartData.getBytes()
        );

        BinaryContent createdBinaryContent = binaryContentDomainService.create(binaryContent);

        log.info(
                "BinaryContent 생성 완료: binaryContentId={}, fileName={}, size={}",
                createdBinaryContent.getId(),
                createdBinaryContent.getFileName(),
                createdBinaryContent.getBytes().length
        );

        return BinaryContentResponseDto.from(createdBinaryContent);
    }

    @Override
    public BinaryContentResponseDto findById(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentDomainService.findById(binaryContentId);
        log.debug(
                "BinaryContent 단건 조회: binaryContentId={}",
                binaryContentId
        );
        return BinaryContentResponseDto.from(binaryContent);
    }

    @Override
    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> binaryContentIds) {
        List<BinaryContentResponseDto> responses =
                binaryContentDomainService.findAllByIdIn(binaryContentIds)
                        .stream()
                        .map(BinaryContentResponseDto::from)
                        .toList();

        log.debug(
                "BinaryContent 목록 조회 완료: requestedCount={}, resultCount={}",
                binaryContentIds.size(),
                responses.size()
        );

        return responses;
    }

    @Override
    public void delete(UUID binaryContentId) {
        log.info(
                "BinaryContent 삭제 시작: binaryContentId={}",
                binaryContentId
        );

        binaryContentDomainService.delete(binaryContentId);

        log.info(
                "BinaryContent 삭제 완료: binaryContentId={}",
                binaryContentId
        );
    }
}
