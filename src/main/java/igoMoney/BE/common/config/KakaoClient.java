package igoMoney.BE.common.config;

import igoMoney.BE.common.jwt.dto.ApplePublicKeyResponse;
import igoMoney.BE.common.jwt.dto.KakaoSignOutRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "kakaoClient", url = "https://kapi.kakao.com/v1", configuration = FeignConfig.class)
public interface KakaoClient {

    @PostMapping(value = "/user/unlink", consumes = "application/x-www-form-urlencoded")
    ApplePublicKeyResponse signOut(@RequestHeader Map<String, String> headers, KakaoSignOutRequest request);
}