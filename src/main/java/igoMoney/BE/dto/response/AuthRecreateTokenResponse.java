package igoMoney.BE.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRecreateTokenResponse {

    private String accessToken;
    private String role;
}
