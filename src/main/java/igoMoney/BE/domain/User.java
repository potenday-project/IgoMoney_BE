package igoMoney.BE.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // 소셜 로그인
    private String loginId; // kakaoId, apple-SUB
    private String provider; // 소셜로그인 구분 위함.
    private String password;

    // 회원 기본 정보
    @Column(nullable = false)
    private String email;
    @Column(unique = true, length = 20)
    private String nickname;
    private String image; // Storage에 저장된 이미지 파일 이름
    private String role; // ROLE_USER 혹은 ROLE_ADMIN
    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;
    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
    }



    // 추가 정보
    @Builder.Default
    private Boolean inChallenge=false;
    private Long myChallengeId; // 사용자가 등록 후 대기중인 챌린지 / 참여중인 챌린지
    @Builder.Default
    private Boolean banned = false;
    private LocalDate banReleaseDate;
    @Builder.Default
    private Integer reportedCount = 0;
    @Builder.Default
    private Integer challengeCount=0;
    @Builder.Default
    private Integer badgeCount=0;
    @Builder.Default
    private Integer winCount=0;

    // UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(this.role));
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }
    @Override
    public String getUsername() {  return String.valueOf(this.id); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {return true; }


    // 회원관리
    public void updateProfileImage(String imageUUID) {
        this.image = imageUUID;
    }

    public void updateNickname(String newNick) {
        this.nickname = newNick;
    }

    public void updateUserStatus(Boolean inChallenge, Long myChallengeId) {
        this.inChallenge = inChallenge;
        this.myChallengeId = myChallengeId;
    }

    public void deleteBadge() {
        if(this.badgeCount >0 ){
            this.badgeCount -=1;
        }
    }
    public void addBadge() { this.badgeCount +=1;}
    public void addChallengeCount() { this.challengeCount +=1;}
    public void addWinCount() { this.winCount +=1;}
    public void subWinCount() {
        if(this.winCount >0) {
            this.winCount -= 1;
        }
    }
    public void addReportedCount() {this.reportedCount +=1; }
    public void resetReportedCount() { this.reportedCount = 0; }
    public void setBanned() { this.banned = true; }
    public void setUnbanned() { this.banned = false; this.banReleaseDate = null; }
    public void setBanReleaseDate(LocalDate date) { this.banReleaseDate = date; }
}
