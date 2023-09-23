package igoMoney.BE.domain;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_user_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user; // ChallengeUser : user = N : 1

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    // 생성 메서드
    public static ChallengeUser createChallengeUser(User user) {

        ChallengeUser challengeUser = new ChallengeUser();
        challengeUser.setUser(user);

        return challengeUser;
    }
}
