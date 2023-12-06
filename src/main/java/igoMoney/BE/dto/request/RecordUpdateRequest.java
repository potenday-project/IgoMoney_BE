package igoMoney.BE.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordUpdateRequest {

    @NotNull
    private Long recordId;
    @NotNull
    private String title;
    private String content;
    private Integer cost;
    private List<MultipartFile> image;
}
