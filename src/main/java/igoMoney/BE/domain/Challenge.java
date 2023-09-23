package igoMoney.BE.domain;

import igoMoney.BE.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Challenge extends BaseEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    private Long leaderId;
    private Long winnerId;
    private Long recordId;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Integer targetAmount;
    private LocalDate startDate;
    @Builder.Default
    private Integer term=10; // days
    private LocalDate endDate;
    @Builder.Default
    private String status="notStarted"; // notStarted/inProgress/cancel/done

    @Builder.Default
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<ChallengeUser> challengeUsers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<Record> records = new ArrayList<>();

    // 연관관계 메서드
    public void addChallengeUser(ChallengeUser challengeUser) {
        challengeUsers.add(challengeUser);
        challengeUser.setChallenge(this);
    }

    // 생성 메서드
    public static Challenge createChallenge(ChallengeUser... challengeUsers) {

        Challenge challenge = new Challenge();
        for (ChallengeUser challengeUser : challengeUsers) {
            challenge.addChallengeUser(challengeUser);
        }
        return challenge;
    }

    public void startChallenge() { this.startDate = LocalDate.now().plusDays(1); this.status = "inProgress";}
    public void stopChallenge() { this.endDate = LocalDate.now(); this.status = "cancel";}
}
