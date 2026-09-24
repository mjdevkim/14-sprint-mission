package com.sprint.mission.mapper;

import com.sprint.mission.controller.dto.response.PageResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class PageResponseMapper {

    public <T> PageResponse<T> toCursorResponse(
            List<T> fetched,
            int size,
            Function<T, Object> cursorExtractor,    // T에서 커서로 쓸 값을 빼낼 함수
                // 예시 - MessageDto::getCreatedAt
            Long totalElements
    ) {
        boolean hasNext = fetched.size() > size;
        List<T> content = hasNext ? fetched.subList(0, size) : fetched;
        Object nextCursor = (!hasNext || content.isEmpty())
                ? null
                : cursorExtractor.apply(content.get(content.size() - 1));

        return new PageResponse<>(content, nextCursor, size, hasNext, totalElements);
    }
}
