package com.sprint.mission.domain;

import com.sprint.mission.domain.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Message extends BaseUpdatableEntity {

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Message N : Channel 1
    @JoinColumn(name = "channel_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)    // 채널이 삭제 -> 메시지도 삭제
    private Channel channel;

    private List<UUID> attachmentIds;   // 첨부파일 리스트

    private Message(
            String content,
            User author,
            Channel channel,
            List<BinaryContent> attachments
    ) {
        this.content = content;
        this.author = author;
        this.channel = channel;
        if (Objects.nonNull(attachments)) {
            this.attachments.addAll(attachments);
        }
    }

    public static Message create(
            String content,
            User author,
            Channel channel,
            List<BinaryContent> attachments
    ) {
        return new Message(content, author, channel, attachments);
    }

    public void updateContent(String content) {
        if (Objects.nonNull(content) && !content.equals(this.content)) {
            this.content = content;
        }
    }
}
