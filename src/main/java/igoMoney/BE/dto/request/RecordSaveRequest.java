package igoMoney.BE.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordSaveRequest {

    @NotNull
    private Long challengeId;
    @NotNull
    private Long userId;
    @NotNull
    private String title;
    private String content;
    private String image;
}
