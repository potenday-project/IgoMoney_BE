package igoMoney.BE.controller;

import igoMoney.BE.common.jwt.JwtAuthorizationFilter;
import igoMoney.BE.common.jwt.dto.TokenDto;
import igoMoney.BE.dto.request.AuthKakaoLoginRequest;
import igoMoney.BE.dto.response.AuthRecreateTokenResponse;
import igoMoney.BE.dto.response.AuthTokenResponse;
import igoMoney.BE.dto.response.IdResponse;
import igoMoney.BE.dto.response.UserResponse;
import igoMoney.BE.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // 카카오 로그인
    @PostMapping("login/kakao/toeken/{accessToken}")
    public ResponseEntity<Void> kakaoLogin(@PathVariable("accessToken") String accessToken) {

        authService.kakaoLogin(accessToken);

        return ResponseEntity(HttpStatus.OK);
    }

    /*
    public ResponseEntity<AuthTokenResponse> kakaoLoginCode(@PathVariable("code") String code) {

        String access_Token = authService.getAccessToken(code);
        authService.kakaoLogin(access_Token);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }*/



    // 애플 로그인
//    @PostMapping("login/apple")
//    public ResponseEntity<AuthTokenResponse> appleLogin(@RequestBody @Valid AuthKakaoLoginRequest request) {
//
//        AuthTokenResponse response = authService.appleLogin(request);
//
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

    // accessToken 재발급
    @PostMapping("token")
    public ResponseEntity<AuthRecreateTokenResponse> refresh(@RequestBody Map<String, String> refreshToken){

        //Refresh Token 검증 및 AccessToken 재발급
        AuthRecreateTokenResponse response = authService.refresh(refreshToken.get("refreshToken"));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 로그아웃
//    @PostMapping("logout")
//    public ResponseEntity<Void> logout(@RequestBody Map<String, String> accessToken) {
//
//        authService.logout(accessToken.get("accessToken"));
//
//        return new ResponseEntity(HttpStatus.OK);
//    }
}
