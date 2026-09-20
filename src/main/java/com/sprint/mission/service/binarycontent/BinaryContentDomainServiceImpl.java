package com.sprint.mission.service.binarycontent;

import com.sprint.mission.domain.BinaryContent;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import com.sprint.mission.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Qualifier("binaryContentService")
public class BinaryContentDomainServiceImpl implements BinaryContentDomainService {

    private final BinaryContentRepository binaryContentRepository;

    public BinaryContentDomainServiceImpl(
            BinaryContentRepository binaryContentRepository
    ) {
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public BinaryContent create(BinaryContent binaryContent) {
        binaryContentRepository.save(binaryContent);
        return binaryContent;
    }

    @Override
    public BinaryContent findById(UUID binaryContentId) {
        if (Objects.isNull(binaryContentId)) {
            throw new DiscodeitException(DiscodeitExceptionType.BINARY_CONTENT_ID_IS_NULL);
        }

        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() ->
                        new DiscodeitException(
                                DiscodeitExceptionType.BINARY_CONTENT_NOT_FOUND,
                                binaryContentId
                        )
                );
    }

    @Override
    public List<BinaryContent> findAll() {
        return binaryContentRepository.findAll();
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAllByIdIn(binaryContentIds);
    }

    @Override
    public void delete(UUID binaryContentId) {
        findById(binaryContentId);  // 없으면 BINARY_CONTENT_NOT_FOUND
        binaryContentRepository.delete(binaryContentId);
    }
}
