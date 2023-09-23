package igoMoney.BE.dto.request;

import lombok.*;

import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeCreateRequest {

    private Long userId;
    @Size(min=5, max=15)
    private String title;
    @Size(max=300)
    private String content;
    private Integer targetAmount;
}
