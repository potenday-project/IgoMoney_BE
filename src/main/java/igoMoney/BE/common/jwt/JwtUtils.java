package igoMoney.BE.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
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

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${application.jwt.access_token.duration}")
    private Long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${application.jwt.refresh_token.duration}")
    private Long REFRESH_TOKEN_EXPIRATION_TIME;

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

        String accessToken = JWT.create()
                .withSubject(String.valueOf(user.getId()))
                .withExpiresAt(new Date(System.currentTimeMillis()+ ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("provider", user.getProvider())
                .withClaim("email", user.getEmail())
                .withClaim("role", user.getRole())
                .sign(Algorithm.HMAC512(secretKey));

        String refreshToken = JWT.create()
                .withSubject(String.valueOf(user.getId()))
                .withExpiresAt(new Date(System.currentTimeMillis()+ REFRESH_TOKEN_EXPIRATION_TIME))
                .withClaim("provider", user.getProvider())
                .withClaim("email", user.getEmail())
                .withClaim("role", user.getRole())
                .sign(Algorithm.HMAC512(secretKey));

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    // Access 토큰 재생성
    public String recreateAccessToken(User user) {

        String accessToken = JWT.create()
                .withSubject(String.valueOf(user.getId()))
                .withExpiresAt(new Date(System.currentTimeMillis()+ ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("provider", user.getProvider())
                .withClaim("email", user.getEmail())
                .withClaim("role", user.getRole())
                .sign(Algorithm.HMAC512(secretKey));

        return accessToken;
    }

    public Long getUserIdFromToken(String token) {

        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
        return Long.parseLong(jwt.getSubject());
    }

    public Map<String, Claim> getClaimsFromToken(String token) {

        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
        return jwt.getClaims();
    }

    // refresh 토큰의 유효성 + 만료일자 확인 -> 유효하면 true 리턴
    public Boolean validateRefreshToken(String token) {

        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (TokenExpiredException e) {
            throw new JwtException("TOKEN_EXPIRED");
        } catch (JWTVerificationException e) {
            throw new JwtException("TOKEN_INVALID");
        }
    }

    // 토큰의 유효성 + 만료일자 확인
    public Boolean validateAccessToken(String token) {

        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;

        } catch (TokenExpiredException e) {
            throw new JwtException("TOKEN_EXPIRED");
        } catch (JWTVerificationException e) {
            throw new JwtException("TOKEN_INVALID");
        }
    }
}
