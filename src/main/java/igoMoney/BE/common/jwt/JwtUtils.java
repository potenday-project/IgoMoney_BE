package igoMoney.BE.common.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import igoMoney.BE.common.jwt.dto.TokenDto;
import igoMoney.BE.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import java.util.Date;
import java.security.Key;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.secret_refresh}")
    private String refreshSecretKey;

    private Key key;

    public JwtUtils(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }


    // 토큰 생성
    public TokenDto createToken(User user) {

        String userkey; userkey = user.getProvider()+"_"+user.getEmail();

        String accessToken = JWT.create()
                .withSubject(String.valueOf(user.getId()))
                .withExpiresAt(new Date(System.currentTimeMillis()+ JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("id", user.getId())
                .withClaim("username", userkey)
                .withClaim("role", user.getRole())
                .sign(Algorithm.HMAC512(secretKey));

        String refreshToken = JWT.create()
                .withSubject(String.valueOf(user.getId()))
                .withExpiresAt(new Date(System.currentTimeMillis()+ JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME))
                .withClaim("id", user.getId())
                .withClaim("username", userkey)
                .withClaim("role", user.getRole())
                .sign(Algorithm.HMAC512(refreshSecretKey));

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .key(userkey).build();
    }

    // Access 토큰 재생성
    public String recreateAccessToken(User user) {

        String userkey; userkey = user.getProvider()+"_"+user.getEmail();
        String accessToken = JWT.create()
                .withSubject(String.valueOf(user.getId()))
                .withExpiresAt(new Date(System.currentTimeMillis()+ JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("id", user.getId())
                .withClaim("username", userkey)
                .withClaim("role", user.getRole())
                .sign(Algorithm.HMAC512(secretKey));

        return accessToken;
    }

    public String getUsernameFromAccessToken(String token) {

        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
        return jwt.getClaim("username").asString();
    }

    public String getUsernameFromRefreshToken(String token) {

        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(refreshSecretKey)).build().verify(token);
        return jwt.getClaim("username").asString();
    }

    // refresh 토큰의 유효성 + 만료일자 확인 -> 유효하면 true 리턴
    public Boolean validateRefreshToken(String token) {

        try {
            JWT.require(Algorithm.HMAC512(refreshSecretKey)).build().verify(token);
            return true;
        } catch (TokenExpiredException e) {
            throw new JwtException("TOKEN_EXPIRED");
        } catch (JWTVerificationException e) {
            throw new JwtException("TOKEN_INVALID");
        }
    }

    // access 토큰의 유효성 + 만료일자 확인 -> 유효하면 남은 유효시간 반환
    public Long  validateAccessToken(String token) {

        try {
            Date expiration = JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token).getExpiresAt(); // 제대로 동작한다면 이 부분 주석 지우기.
            Long now = new Date().getTime();
            // accessToken 의 현재 남은시간 반환
            return (expiration.getTime() - now);

        } catch (TokenExpiredException e) {
            throw new JwtException("TOKEN_EXPIRED");
        } catch (JWTVerificationException e) {
            throw new JwtException("TOKEN_INVALID");
        }
    }
}
