package igoMoney.BE.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeCreateRequest {

    private Long userId;
    private String title;
    private String content;
    private Integer targetAmount;
    private Integer categoryId;
    private LocalDate startDate;
}
