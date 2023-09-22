package igoMoney.BE.service;

import igoMoney.BE.common.exception.CustomException;
import igoMoney.BE.common.exception.ErrorCode;
import igoMoney.BE.common.jwt.JwtUtils;
import igoMoney.BE.domain.RefreshToken;
import igoMoney.BE.domain.User;
import igoMoney.BE.dto.response.AuthRecreateTokenResponse;
import igoMoney.BE.dto.response.UserResponse;
import igoMoney.BE.repository.UserRepository;
import igoMoney.BE.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    //private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    // 애플 회원가입
    public void AppleSignUp(String sub, String email) {

        // DB에 data에서 받아온 정보를 가진 사용자가 있는지 조회
        User findUser = userRepository.findByEmailAndProvider(email, "apple");

        // DB에 사용자가 없다면, 애플 로그인을 처음 한 사용자이니, DB에 사용자 정보를 저장(회원가입 시켜줌)
        if (findUser == null) {

            User user = User.builder()
                    .provider("apple")
                    .loginId(sub) // ID 토큰의 sub
                    .email(email)
                    .role("ROLE_USER")
                    .build();

            findUser = userRepository.save(user);
        }
    }

    // 카카오
    public String getAccessToken (String authorize_code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //    POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //    POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=b5f85af25d1bdf961d4f2016bafe3c6e");
            sb.append("&redirect_uri=http://localhost:8000/login");
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return access_Token;
    }


    // 카카오 로그인 & 회원가입
    public UserResponse kakaoLogin(String accessToken) throws IOException {

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

            int id = element.getAsJsonObject().get("id").getAsInt();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            boolean hasProfile = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_profile").getAsBoolean();
            String email = "";
            String image = "";
            String nickname = "";
            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }
            if(hasProfile){
                image = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("profile_image_url").getAsString();
                nickname = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("nickname").getAsString();
            }
            System.out.println("id : " + id);
            System.out.println("email : " + email);
            System.out.println("image : " + image);
            System.out.println("nickname : " + nickname);
            br.close();

            // DB에 data에서 받아온 정보를 가진 사용자가 있는지 조회
            User findUser = userRepository.findByEmailAndProvider(email, "kakao");

            // DB에 사용자가 없다면, 구글 로그인을 처음 한 사용자이니, DB에 사용자 정보를 저장(회원가입 시켜줌)
            if (findUser == null) {

                User user = User.builder()
                        .provider("kakao")
                        .email(email)
                        .image(image)
                        .nickname(nickname)
                        .role("ROLE_USER")
                        .build();

                findUser = userRepository.save(user);
            }
            UserResponse response = UserResponse.builder()
                    .id(findUser.getId())
                    .email(findUser.getEmail())
                    .image(findUser.getImage())
                    .nickname(findUser.getNickname())
                    .role(findUser.getRole())
                    .provider(findUser.getProvider())
                    .build();
            return response;

        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.LOGIN_CONNECTION_ERROR);
        }
    }




    // [카카오] accessToken 재발급
    public AuthRecreateTokenResponse refresh(String request) {

        String refreshToken = request.replace("Bearer ", "");

        // refresh 토큰 유효한지 확인
        jwtUtils.validateRefreshToken(refreshToken);
        String loginId = jwtUtils.getUsernameFromRefreshToken(refreshToken);
        RefreshToken findRefreshToken = refreshTokenRepository.findByKeyLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_INVALID));
        if (!refreshToken.equals(findRefreshToken.getRefreshToken())) {
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }

        User findUser = userRepository.findByLoginId(loginId);

        String createdAccessToken = jwtUtils.recreateAccessToken(findUser);

        if (createdAccessToken == null) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        }

        AuthRecreateTokenResponse response = AuthRecreateTokenResponse.builder()
                .accessToken(createdAccessToken)
                .role(findUser.getRole())
                .build();

        return response;
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

}
