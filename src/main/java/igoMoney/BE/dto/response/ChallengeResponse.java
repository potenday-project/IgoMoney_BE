package igoMoney.BE.dto.response;

import jakarta.validation.constraints.Size;
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
    private Long recordId;
    private String title;
    private String content;
    private Integer targetAmount;
    private LocalDate startDate;
    private Integer term;
    private LocalDate endDate;
}
