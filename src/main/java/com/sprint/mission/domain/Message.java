package com.sprint.mission.domain;

import com.sprint.mission.domain.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
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
        // DB에서 부모가 지워지면 자식도 지운다 - hibernate가 테이블을 만들 때 이 컬럼을 on delete cascade로 바꿈
    private Channel channel;    // 얘가 더 상위개념인거임. 얘가 삭제되면 지금 클래스(메세지)도 삭제되는거

    @ManyToOne(fetch = FetchType.LAZY)  // Message N : User 1
    @JoinColumn(name = "author_id")
    @OnDelete(action = OnDeleteAction.SET_NULL) // 작성자가 삭제되어도 메시지는 남고 author_id=null로
        // DB에서 부모가 지워져도 자식은 살아있다 - fk 컬럼만 null로 바꿈
    private User author;

    @BatchSize(size = 100)   // attachments는 LAZY 컬렉션(@OneToMany 기본값)이라 메시지 목록을 순회하며
                              // 접근하면 메시지 수만큼 쿼리가 따로 나간다(N+1). 배치사이즈를 걸면
                              // "WHERE message_id IN (?, ?, ...)" 한 번으로 최대 100건씩 묶어서 가져온다.
    @OneToMany(cascade = {      // Message 1 : BinaryContent N
            CascadeType.PERSIST,    // "Message 저장을 BinaryContent에 전파한다"
            CascadeType.REMOVE      // "Message 삭제를 BinaryContent에 전파한다"
    })
    // 중간테이블 필요 -- Message 1 : BinaryContent N 인데, BinaryContent는 프로필 이미지도 나타내기 때문에 Message와 독립적인 엔티티임
    @JoinTable( // Hr bank 이렇게 했으면 엔티티 추가로 만든거 안해도 됐을 것.. -- 다음 프로젝트 때는 참고하자
            name = "message_attachments",           // 중간 테이블 이름
            joinColumns = @JoinColumn(  // 현재 엔티티(Message)와 중간 테이블간의 외래키 정의
                    name = "message_id",
                    nullable = false
            ),
            inverseJoinColumns = @JoinColumn(   // 반대쪽 엔티티(BinaryContent)와 중간 테이블간의 외래키 정의
                    name = "attachment_id",
                    nullable = false
            )
    )
    private List<BinaryContent> attachments = new ArrayList<>();

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
