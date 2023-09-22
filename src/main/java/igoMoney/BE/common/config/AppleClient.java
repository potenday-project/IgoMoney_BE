package igoMoney.BE.common.config;

import igoMoney.BE.common.jwt.dto.AppleTokenRequest;
import igoMoney.BE.common.jwt.dto.AppleTokenResponse;
import igoMoney.BE.common.jwt.dto.ApplePublicKeyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "appleClient", url = "https://appleid.apple.com/auth", configuration = FeignConfig.class)
public interface AppleClient {
    @GetMapping(value = "/keys")
    ApplePublicKeyResponse getAppleAuthPublicKey();

    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
    AppleTokenResponse getToken(AppleTokenRequest request);
}
