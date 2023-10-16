package igoMoney.BE.common.config;

import igoMoney.BE.common.jwt.dto.ApplePublicKeyResponse;
import igoMoney.BE.common.jwt.dto.KakaoSignOutRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "kakaoClient", url = "https://localhost:8080", configuration = FeignConfig.class)
public interface KakaoClient {

    @PostMapping(value = "/user/unlink")
    ApplePublicKeyResponse signOut(@RequestHeader Map<String, String> headers, KakaoSignOutRequest request);
}