package igoMoney.BE.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FromAppleService {

    private String state;
    private String code;
    private String id_token;
    private String user;
    private String refresh_token;
    private Long userId;
}
