package igoMoney.BE.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    private Long id;
    private String nickname;
//    private MultipartFile image;
//    private Boolean isChanged; // image 수정 여부 체크. true면 이미지 수정된 것.
}
