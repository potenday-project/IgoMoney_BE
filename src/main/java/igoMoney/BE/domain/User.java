package igoMoney.BE.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import igoMoney.BE.common.entity.BaseEntity;
import igoMoney.BE.dto.request.UserUpdateRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    // 소셜 로그인
    private String loginId;
    private String provider; // 소셜로그인 구분 위함.

    // 회원 기본 정보
    @Column(nullable = false, unique = true)
    private String email;
    @Column(unique = true, length = 20)
    private String nickname;
    private String image; // Storage에 저장된 이미지 파일 이름
    private String role; // ROLE_USER 혹은 ROLE_ADMIN

    // 추가 정보
    @Builder.Default
    private Boolean inChallenge=false;
    @Builder.Default
    private Integer challengeCount=0;
    @Builder.Default
    private Integer badgeCount=0;
    @Builder.Default
    private Integer winCount=0;

    public void updateUser(UserUpdateRequest request) {
        this.nickname = request.getNickname();
    }
}
