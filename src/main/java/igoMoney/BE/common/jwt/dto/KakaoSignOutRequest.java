package igoMoney.BE.common.jwt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoSignOutRequest {

    @Builder.Default
    private String target_id_type = "user_id";
    private Long target_id; // kakaoId (User-loginId)
}
