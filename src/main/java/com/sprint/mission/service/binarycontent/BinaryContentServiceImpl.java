package com.sprint.mission.service.binarycontent;

import com.sprint.mission.controller.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.storage.BinaryContentStorage;
import com.sprint.mission.domain.BinaryContent;
import com.sprint.mission.mapper.BinaryContentMapper;
import com.sprint.mission.multipart.MultipartFileConverter;
import com.sprint.mission.multipart.MultipartFileDto;
import com.sprint.mission.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class BinaryContentServiceImpl implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final MultipartFileConverter multipartFileConverter;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentDto create(MultipartFile multipartFile) {
        MultipartFileDto sanitizedMultipartData = multipartFileConverter.convert(multipartFile);

        BinaryContent binaryContent = BinaryContent.create(
                sanitizedMultipartData.getFileName(),
                sanitizedMultipartData.getContentType(),
                sanitizedMultipartData.getBytes().length
        );

        BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
        binaryContentStorage.put(createdBinaryContent.getId(), sanitizedMultipartData.getBytes());

        log.info(
                "BinaryContent 생성 완료: binaryContentId={}, fileName={}, size={}",
                createdBinaryContent.getId(),
                createdBinaryContent.getFileName(),
                createdBinaryContent.getSize()
        );

        return binaryContentMapper.toDto(createdBinaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto findById(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.getBinaryContent(binaryContentId);
        log.debug(
                "BinaryContent 단건 조회: binaryContentId={}",
                binaryContentId
        );
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        List<BinaryContentDto> responses =
                binaryContentRepository.findAllByIdIn(binaryContentIds)
                        .stream()
                        .map(binaryContentMapper::toDto)
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

        binaryContentRepository.getBinaryContent(binaryContentId);
        binaryContentRepository.deleteById(binaryContentId);

        log.info(
                "BinaryContent 삭제 완료: binaryContentId={}",
                binaryContentId
        );
    }
}
