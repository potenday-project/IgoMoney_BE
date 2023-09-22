package igoMoney.BE.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetaInfoForm {

    private String client_id;
    private String redirect_uri;
    private String nonce;
}
