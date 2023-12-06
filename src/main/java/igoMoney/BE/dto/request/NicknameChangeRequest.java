package igoMoney.BE.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NicknameChangeRequest {

    @NotNull
    private Long userId;
    @NotBlank(message = "닉네임은 공백일 수 없습니다.")
    private String nickname;
}
