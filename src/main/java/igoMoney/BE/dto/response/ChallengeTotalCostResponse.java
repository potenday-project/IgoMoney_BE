package igoMoney.BE.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeTotalCostResponse {

    private Long userId;
    private Integer totalCost;
}
