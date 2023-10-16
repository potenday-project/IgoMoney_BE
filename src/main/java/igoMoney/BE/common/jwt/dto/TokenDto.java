package igoMoney.BE.common.jwt.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto { // 서버 자체 토큰

    private String accessToken;
    private String refreshToken;
    private String provider_accessToken;
    private Long userId;
}
