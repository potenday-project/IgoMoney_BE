package igoMoney.BE.common.jwt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppleSignOutRequest {

    private Long userId;
    private String client_id; // Apple app Bundle ID ex. com.test.igo
    private String client_secret;
    private String token; // refresh token or access token
    private String token_type_hint; // access_token, refresh_token
}
