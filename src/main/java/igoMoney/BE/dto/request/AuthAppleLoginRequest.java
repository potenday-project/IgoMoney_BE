package igoMoney.BE.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthAppleLoginRequest {

    @NotNull
    private String id;
    @NotNull
    private String email;
    @NotNull
    private String nickname;
    private String picture	;
    private String code;
}
