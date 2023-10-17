package igoMoney.BE.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @NotNull
    private Long id;
    @NotEmpty
    private String nickname;
    private MultipartFile image;
    @NotNull
    private Boolean imageChanged; // 프로필 이미지 변경 여부
}
