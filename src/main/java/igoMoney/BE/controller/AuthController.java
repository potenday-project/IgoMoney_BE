package igoMoney.BE.controller;

import igoMoney.BE.common.jwt.AppleJwtUtils;
import igoMoney.BE.common.jwt.dto.AppleSignOutRequest;
import igoMoney.BE.common.jwt.dto.AppleTokenResponse;
import igoMoney.BE.common.jwt.dto.TokenDto;
import igoMoney.BE.dto.request.FromAppleService;
import igoMoney.BE.dto.response.AuthRecreateTokenResponse;
import igoMoney.BE.service.AuthService;
import igoMoney.BE.service.ChallengeService;
import igoMoney.BE.service.RecordService;
import igoMoney.BE.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ChallengeService challengeService;
    private final RecordService recordService;
    private final AppleJwtUtils appleJwtUtils;
    private final RefreshTokenService refreshTokenService;

    // 카카오 로그인
    @PostMapping("login/kakao")
    @ResponseBody
    public ResponseEntity<TokenDto> kakaoLogin(@RequestBody TokenDto accessToken) throws IOException {

        TokenDto response = authService.kakaoLogin(accessToken.getAccessToken());
        refreshTokenService.saveRefreshToken(response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 카카오 로그인 테스트용
    @PostMapping("login/kakao/redirect")
    @ResponseBody
    public ResponseEntity<Void> kakaoLoginPage(@RequestParam String code){

        // 인가코드(code) 받기
        System.out.println(">>> Kakao Code: "+code);

        // 인가토큰(accessToken) 요청하기
        authService.kakaoGetAccessToken(code);

        return new ResponseEntity(HttpStatus.OK);
    }

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
    public ResponseEntity<TokenDto> getAppleUserIdToken(@RequestBody FromAppleService fromAppleService) throws Exception {

        // 3. public key 요청하기 (n, e 값 받고 키 생성)
        // 4. Identity Token (JWT) 검증하기
        // 5. ID토큰 payload 바탕으로 회원가입
        List<String> subNemail = appleJwtUtils.checkIdToken(fromAppleService.getId_token());
        // DB에 data에서 받아온 정보를 가진 사용자가 있는지 조회 & 회원가입
        // 6. 서버에서 직접 JWT 토큰 발급하기 (access & refresh token)
        TokenDto response =  authService.AppleSignUp(subNemail); // sub, email
        refreshTokenService.saveRefreshToken(response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // [애플]  회원탈퇴
    @PostMapping("signout/apple")
    @ResponseBody
    public ResponseEntity<Void> appleSignOut(@RequestBody FromAppleService request) throws IOException {

        // 챌린지 중단 및 패배처리
        challengeService.giveUpChallengeSignOut(request.getUserId());
        // User가 작성한 모든 Challenge record 삭제
        recordService.deleteAllUserRecords(request.getUserId());


        // 애플 연동해제 및 회원정보(User) 삭제
        String client_secret = appleJwtUtils.makeClientSecret();
        AppleTokenResponse response = appleJwtUtils.requestCodeValidations(request.getUserId(), client_secret, request.getCode(), null);

        AppleSignOutRequest appleRequest = AppleSignOutRequest.builder()
                .userId(request.getUserId())
                .client_secret(client_secret)
                .token_type_hint("refresh_token")
                .token(response.getRefresh_token())
                .build();
        authService.appleSignOut(appleRequest);

        return new ResponseEntity(HttpStatus.OK);
    }

    // 카카오 회원탈퇴
    @PostMapping("signout/kakao/{userId}")
    @ResponseBody
    public ResponseEntity<Void> kakaoSignOut(@PathVariable Long userId){

        // 챌린지 중단 및 패배처리
        challengeService.giveUpChallengeSignOut(userId);
        // User가 작성한 모든 Challenge record 삭제
        recordService.deleteAllUserRecords(userId);
        // 카카오 연동해제 및 회원정보(User) 삭제
        authService.kakaoSignOut(userId);

        return new ResponseEntity(HttpStatus.OK);
    }

    // refresh token으로 accessToken 재발급
    @PostMapping("refresh-token")
    @ResponseBody
    public ResponseEntity<AuthRecreateTokenResponse> refreshToken(@RequestBody Map<String, String> refreshToken){

        AuthRecreateTokenResponse response = authService.refreshToken(refreshToken.get("refreshToken"));

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
