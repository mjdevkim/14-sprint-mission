package com.sprint.mission.domain;

import com.sprint.mission.domain.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class User extends BaseUpdatableEntity {

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 60)
    private String password;

    // 프로필 이미지가 삭제되면 profile_id 필드만 NULL 로 바뀐다 -- User는 유지됨
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", unique = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private BinaryContent profile;

    @OneToOne(
            mappedBy = "user",  // user status가 user fk를 가진다 (user status가 주인)
            cascade = CascadeType.ALL,  // 부모에게 실행되는 걸 자식에게 다 전파
            orphanRemoval = true    // orphan 자동 삭제
    )
    private UserStatus status;

    // UserRequestDto 통해서 User 객체를 생성할것이기 때문에 private으로
    private User(
            String username,
            String email,
            String password,
            BinaryContent profile
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.status = UserStatus.create(this);  // 모든 User는 UserStatus를 가짐
    }


    // 이 정적 메소드를 통해서 User 객체 생성
    public static User create(
            String username,
            String email,
            String password,
            BinaryContent profile
    ) {
        return new User(
                username,
                email,
                password,
                profile
        );
    }


    public void updateAccountDetails(
            String username,
            String email,
            String password,
            BinaryContent profile
    ) {

        if (Objects.nonNull(username)
                && !Objects.equals(username, this.username)) {
            this.username = username;
        }

        if (Objects.nonNull(email)
                && !Objects.equals(email, this.email)) {
            this.email = email;
        }

        if (Objects.nonNull(password)
                && !Objects.equals(password, this.password)) {
            this.password = password;
        }

        if (Objects.nonNull(profile)
                && !Objects.equals(profile, this.profile)) {
            this.profile = profile;
        }
    }

    public boolean matchesPassword(String password) {
        return Objects.equals(this.password, password);
    }
}
