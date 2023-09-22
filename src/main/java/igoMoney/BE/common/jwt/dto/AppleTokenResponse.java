package igoMoney.BE.common.jwt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppleTokenResponse {

    private String access_token;
    private String expires_in;
    private String id_token;
    private String refresh_token;
    private String token_type;
    private String error;
}
