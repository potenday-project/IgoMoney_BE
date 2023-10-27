package igoMoney.BE.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordUpdateRequest {

    @NotNull
    private Long recordId;
    @NotNull @Size(min=5, max=15)
    private String title;
    @Size(max=300)
    private String content;
    private Integer cost;
    private MultipartFile image;
}
