package com.sprint.mission.domain;

import com.sprint.mission.domain.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "binary_contents")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class BinaryContent extends BaseEntity {

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    private BinaryContent(String fileName, String contentType, long size) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
    }

    public static BinaryContent create(String fileName, String contentType, long size) {
        return new BinaryContent(fileName, contentType, size);
    }
}
