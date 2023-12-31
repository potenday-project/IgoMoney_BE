package igoMoney.BE.common.jwt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppleTokenRequest {

    private String code;
    private String client_id; // Apple app Bundle ID ex. com.test.igo
    private String client_secret;
    private String grant_type; // authorization_code, refresh_token
    private String refresh_token;
}
