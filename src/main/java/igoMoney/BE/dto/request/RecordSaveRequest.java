package igoMoney.BE.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull @Size(min=5, max=15)
    private String title;
    @Size(max=300)
    private String content;
    private Integer cost;
    private String image;
}
