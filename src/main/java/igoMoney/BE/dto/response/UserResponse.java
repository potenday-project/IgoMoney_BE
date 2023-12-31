package igoMoney.BE.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String provider;
    private String email;
    private String nickname;
    private String image;
    private String role;

    private Boolean inChallenge;
    private Long myChallengeId;
    private Integer challengeCount;
    private Integer winCount;
    private Integer badgeCount;
}
