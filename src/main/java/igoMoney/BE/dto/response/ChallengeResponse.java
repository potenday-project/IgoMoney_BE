package igoMoney.BE.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeResponse {

    private Long id;
    private Long leaderId;
    private Long winnerId;
    private Long competitorId; // 상대방 ID
    private Long recordId;
    private String title;
    private String content;
    private Integer targetAmount;
    private Integer categoryId;
    private LocalDate startDate;
    private Integer term;
    private LocalDate endDate;
}
