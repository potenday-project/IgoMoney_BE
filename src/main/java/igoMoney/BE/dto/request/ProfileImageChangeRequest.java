package igoMoney.BE.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileImageChangeRequest {

    @NotNull
    private Long userId;
    @NotNull
    private MultipartFile image;
}
