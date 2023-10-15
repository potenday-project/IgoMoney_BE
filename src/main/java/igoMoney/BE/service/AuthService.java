package igoMoney.BE.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import igoMoney.BE.common.config.AppleClient;
import igoMoney.BE.common.exception.CustomException;
import igoMoney.BE.common.exception.ErrorCode;
import igoMoney.BE.common.jwt.AppleJwtUtils;
import igoMoney.BE.common.jwt.JwtUtils;
import igoMoney.BE.common.jwt.dto.AppleSignOutRequest;
import igoMoney.BE.common.jwt.dto.TokenDto;
import igoMoney.BE.domain.RefreshToken;
import igoMoney.BE.domain.User;
import igoMoney.BE.dto.response.AuthRecreateTokenResponse;
import igoMoney.BE.repository.RefreshTokenRepository;
import igoMoney.BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    //private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final AppleJwtUtils appleJwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AppleClient appleClient;

    @Value("${spring.security.oauth2.client.registeration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registeration.kakao.client-secret}")
    private String kakaoClientSecret;

    // 애플 회원가입
    public TokenDto AppleSignUp(List<String> subNemail) {

        // DB에 data에서 받아온 정보를 가진 사용자가 있는지 조회
        User findUser = userRepository.findByEmailAndProvider(subNemail.get(1), "apple");

        // DB에 사용자가 없다면, 애플 로그인을 처음 한 사용자이니, DB에 사용자 정보를 저장(회원가입 시켜줌)
        if (findUser == null) {

            User user = User.builder()
                    .provider("apple")
                    .loginId(subNemail.get(0)) // ID 토큰의 sub
                    .email(subNemail.get(1))
                    .role("ROLE_USER")
                    .build();

            userRepository.save(user);
            findUser = user;
        }
        return jwtUtils.createToken(findUser);// Save User token
    }

    // 카카오 - 백엔드 test용
    public String kakaoGetAccessToken (String authorize_code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //    POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            //    POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id="+kakaoClientId);
            sb.append("&client_secret="+kakaoClientSecret);
            sb.append("&redirect_uri=http://localhost:8080/auth/login/kakao/redirect");
            sb.append("&code=" + authorize_code);
            bw.write(sb.toString());
            bw.flush();

            //    결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //    요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //    Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }


    // 카카오 로그인 & 회원가입
    public TokenDto kakaoLogin(String accessToken) throws IOException {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            int id = element.getAsJsonObject().get("id").getAsInt(); // 카카오 회원번호
            String email = "";
            String image = "";
            //String nickname = "";
            email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            image = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("profile_image_url").getAsString();
            //nickname = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("nickname").getAsString();

            System.out.println("id : " + id);
            System.out.println("email : " + email);
            System.out.println("image : " + image);
            //System.out.println("nickname : " + nickname);
            br.close();

            // DB에 data에서 받아온 정보를 가진 사용자가 있는지 조회
            User findUser = userRepository.findByEmailAndProvider(email, "kakao");

            // DB에 사용자가 없다면, 구글 로그인을 처음 한 사용자이니, DB에 사용자 정보를 저장(회원가입 시켜줌)
            if (findUser == null) {

                User user = User.builder()
                        .provider("kakao")
                        .email(email)
                        .image(image)
                        //.nickname(nickname)
                        .role("ROLE_USER")
                        .build();

                userRepository.save(user);
                findUser = user;
            }
            return jwtUtils.createToken(findUser);// Save User token

        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.LOGIN_CONNECTION_ERROR);
        }
    }




    // accessToken 재발급
    public AuthRecreateTokenResponse refreshToken(String refresh) {

        String refreshToken = refresh.replace("Bearer ", "");

        // refresh 토큰 유효한지 확인
        jwtUtils.validateRefreshToken(refreshToken);
        Long userId = jwtUtils.getUserIdFromToken(refreshToken);
        RefreshToken findRefreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_INVALID));
        if (!refreshToken.equals(findRefreshToken.getRefreshToken())) {
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }

        User findUser = getUserOrThrow(userId);
        String createdAccessToken = jwtUtils.recreateAccessToken(findUser);

        AuthRecreateTokenResponse response = AuthRecreateTokenResponse.builder()
                .accessToken(createdAccessToken)
                .role(findUser.getRole())
                .build();

        return response;
    }

    // 애플 회원탈퇴
    public void appleSignOut(AppleSignOutRequest request) throws IOException {

        // 애플 연동해제
        request.setClient_id(appleJwtUtils.getClientId());
        request.setClient_secret(appleJwtUtils.makeClientSecret());
        appleClient.signOut(request);

        // User 정보 삭제
        User findUser = getUserOrThrow(request.getUserId());
        userRepository.delete(findUser);
    }

    // 로그아웃
//    public void logout(String request) {
//
//        String accessToken = request.replace("Bearer ", "");
//        Long expiration = jwtUtil.validateAccessToken(accessToken);
//
//        redisTemplate.opsForValue()
//                .set(accessToken, "blackList", expiration, TimeUnit.MILLISECONDS);
//    }

    // 예외 처리 - 존재하는 user인지
    public User getUserOrThrow(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }
}
