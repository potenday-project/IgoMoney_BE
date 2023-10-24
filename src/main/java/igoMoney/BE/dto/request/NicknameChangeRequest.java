package igoMoney.BE.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[^\n\t\r\f]{3,8}$", message = "닉네임은 \\t, \\r, \\n, \\f 제외 최소3자 최대 8자여야 합니다.")
    private String nickname;
}
