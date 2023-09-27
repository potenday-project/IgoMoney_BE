package igoMoney.BE.controller;

import igoMoney.BE.common.jwt.AppleJwtUtils;
//import igoMoney.BE.common.jwt.JwtAuthorizationFilter;
//import igoMoney.BE.common.jwt.dto.AppleTokenRequest;
import igoMoney.BE.common.jwt.dto.AppleTokenResponse;
import igoMoney.BE.common.jwt.dto.TokenDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import igoMoney.BE.dto.request.AuthKakaoLoginRequest;
import igoMoney.BE.dto.request.FromAppleService;
import igoMoney.BE.dto.response.AuthRecreateTokenResponse;
import igoMoney.BE.dto.response.AuthTokenResponse;
import igoMoney.BE.dto.response.IdResponse;
import igoMoney.BE.dto.response.UserResponse;
import igoMoney.BE.service.AuthService;
//import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AppleJwtUtils appleJwtUtils;
    //private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    // 카카오 로그인
    @PostMapping("login/kakao/token/{accessToken}")
    @ResponseBody
    public ResponseEntity<Void> kakaoLogin(@PathVariable("accessToken") String accessToken) throws IOException {

        authService.kakaoLogin(accessToken);

        return new ResponseEntity(HttpStatus.OK);
    }

    /*
    public ResponseEntity<AuthTokenResponse> kakaoLoginCode(@PathVariable("code") String code) {

        String access_Token = authService.getAccessToken(code);
        authService.kakaoLogin(access_Token);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }*/

    // 1. 애플 로그인 페이지 - test용
    @GetMapping("login/apple/page")
    public String appleLoginPage(ModelMap model) {

        Map<String, String> metaInfo = appleJwtUtils.getLoginMetaInfo();

        model.addAttribute("client_id", metaInfo.get("CLIENT_ID"));
        model.addAttribute("redirect_uri", metaInfo.get("REDIRECT_URI"));
        model.addAttribute("nonce", metaInfo.get("NONCE"));

        return "appleIndex";
    }

    // 2. 로그인 후 Identity Token, AuthorizationCode 받기
    @PostMapping("login/apple/redirect")
    @ResponseBody
    public ResponseEntity<AppleTokenResponse> getAppleUserIdToken(@RequestBody FromAppleService fromAppleService) throws Exception {

        if (fromAppleService == null) {
            return null;
        }

        String code = fromAppleService.getCode();
        String refresh_token = fromAppleService.getRefresh_token();

        // 3. client secret 토큰 만들기
        String client_secret = appleJwtUtils.makeClientSecret();

        logger.debug("================================");
        logger.debug("id_token ‣ " + fromAppleService.getId_token());
        logger.debug("client_secret ‣ " + client_secret);
        logger.debug("================================");

        // 4. public key 요청하기 (n, e 값 받고 키 생성)
        // 5. Identity Token (JWT) 검증하기
        // 6. ID토큰 payload 바탕으로 회원가입
        Long userId = appleJwtUtils.checkIdToken(fromAppleService.getId_token());

        // 7. Authorization Code로 JWT 토큰 발급받기
        AppleTokenResponse response = appleJwtUtils.requestCodeValidations(userId, client_secret, code, refresh_token);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // [애플]  회원탈퇴


    // [카카오] refresh token으로 accessToken 재발급
    @PostMapping("token")
    @ResponseBody
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
