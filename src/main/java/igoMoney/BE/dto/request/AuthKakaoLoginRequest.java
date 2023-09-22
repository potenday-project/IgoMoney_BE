package igoMoney.BE.dto.request;


//import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthKakaoLoginRequest {

    private String id;
    private String email;
    private String nickname;
    private String picture	;
    private String code;
}
