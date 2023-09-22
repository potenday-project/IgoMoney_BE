package igoMoney.BE.common.jwt;

import com.auth0.jwt.interfaces.Payload;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import igoMoney.BE.common.config.AppleClient;
import igoMoney.BE.common.exception.CustomException;
import igoMoney.BE.common.exception.ErrorCode;
import igoMoney.BE.common.jwt.dto.AppleTokenRequest;
import igoMoney.BE.common.jwt.dto.AppleTokenResponse;
import igoMoney.BE.common.jwt.dto.ApplePublicKeyResponse;
import igoMoney.BE.service.AuthService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleJwtUtils extends JwtUtils {

    private final AppleClient appleClient;
    private final AuthService authService;

    private String ISS = "https://appleid.apple.com";

    @Value("${spring.security.oauth2.client.registeration.apple.key-id}")
    private String keyId;

    @Value("${spring.security.oauth2.client.registeration.apple.p8key}")
    private String p8PrivateKey;

    @Value("${spring.security.oauth2.client.registeration.apple.team-id}")
    private String teamId;

    @Value("${spring.security.oauth2.client.registeration.apple.client-id}")
    private String clientId; // AUD


    @Value("${spring.security.oauth2.client.registeration.apple.redirect-url}")
    private String REDIRECT_URL;

    @Override
    public Claims getClaims(String id_Token) {

        try {
            // 애플서버에 public key(n,e값) 요청
            ApplePublicKeyResponse response = appleClient.getAppleAuthPublicKey();

            String headerOfIdentityToken = id_Token.substring(0, id_Token.indexOf("."));
            Map<String, String> header = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(headerOfIdentityToken), "UTF-8"), Map.class);
            ApplePublicKeyResponse.Key key = response.getMatchedKeyBy(header.get("kid"), header.get("alg"))
                    .orElseThrow(() -> new NullPointerException("Failed get public key from apple's id server."));

            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            // public key 생성
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            // 유효성 검증
            return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(id_Token).getBody();

        } catch (NoSuchAlgorithmException e) {
        } catch (InvalidKeySpecException e) {
        } catch (SignatureException e) {
        } catch (MalformedJwtException e) {
            //토큰 서명 검증 or 구조 문제 (Invalid token)
            throw new CustomException(ErrorCode.ID_TOKEN_INVALID);
        } catch (ExpiredJwtException e) {
            //토큰이 만료됐기 때문에 클라이언트는 토큰을 refresh 해야함.
            throw new CustomException(ErrorCode.ID_TOKEN_EXPIRED);
        } catch (Exception e) {
        }

        return null;
    }

    // p8키로 client secret 만들기
    public String makeClientSecret() throws IOException {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setHeaderParam("kid", keyId)
                .setHeaderParam("alg", "ES256")
                .setIssuer(teamId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .setAudience("https://appleid.apple.com")
                .setSubject(clientId)
                .signWith(SignatureAlgorithm.ES256, getPrivateKey())
                .compact();
    }

    // .p8 키
    private PrivateKey getPrivateKey() throws IOException {
        ClassPathResource resource = new ClassPathResource("p8_key.txt");
        // String privateKey = new String(Files.readAllBytes(Paths.get(resource.getURI())));
        String privateKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
        Reader pemReader = new StringReader(privateKey);
        PEMParser pemParser = new PEMParser(pemReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        return converter.getPrivateKey(object);
    }

    public Payload decodeFromIdToken(String id_token) {

        try {
            SignedJWT signedJWT = SignedJWT.parse(id_token);
            ReadOnlyJWTClaimsSet getPayload = signedJWT.getJWTClaimsSet();
            ObjectMapper objectMapper = new ObjectMapper();
            Payload payload = objectMapper.readValue(getPayload.toJSONObject().toJSONString(), Payload.class);

            if (payload != null) {
                return payload;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.ID_TOKEN_INVALID);
        }

        return null;
    }

    public AppleTokenResponse requestCodeValidations(String client_secret, String code, String refresh_token) throws IOException {

        // requestCodeValidations
        if (client_secret != null && code != null && refresh_token == null) {
            AppleTokenRequest request = AppleTokenRequest.builder()
                    .client_id(clientId)
                    .client_secret(client_secret)
                    .code(code)
                    .grant_type("authorization_code")
                    .build();

            return getTokenResponse(request);

        } else if (client_secret != null && refresh_token != null) { // refresh token 있음
            // validateAnExistingRefreshToken
            AppleTokenRequest request = AppleTokenRequest.builder()
                    .client_id(clientId)
                    .client_secret(client_secret)
                    .grant_type("refresh_token")
                    .refresh_token(refresh_token)
                    .build();

            return getTokenResponse(request);
        }
        return null;
    }

    // 1) Authorization code로 토큰 발급받기
    // 2) refresh token으로 access token 재발급
    public AppleTokenResponse getTokenResponse(AppleTokenRequest appleTokenRequest) {

        AppleTokenResponse response = appleClient.getToken(appleTokenRequest);
        return response;
    }

    public void checkIdToken(String id_token) {

        Jws<Claims> jws = null;
        jws = (Jws<Claims>) getClaims(id_token);

        // EXP
        Date currentTime = new Date(System.currentTimeMillis());
        if (!currentTime.before((Date) jws.getBody().get("exp"))) {
            throw new CustomException(ErrorCode.ID_TOKEN_INVALID);
        }

        // ISS, AUD
        if (!ISS.equals(jws.getBody().get("iss")) || !clientId.equals(jws.getBody().get("AUD"))) {
            throw new CustomException(ErrorCode.ID_TOKEN_INVALID);
        }

        String sub = String.valueOf(jws.getBody().get("sub")); // user 식별자
        String email = String.valueOf(jws.getBody().get("email"));

        // 애플로 로그인 시에 본인 이메일을 hide시킬 수 있는데,
        // 그럴 경우 유저의 email 정보가 'example@privaterelay.appleid.com'와 같이 오게 된다.
        String email_verified = String.valueOf(jws.getBody().get("email_verified"));
        String is_private_email = String.valueOf(jws.getBody().get("is_private_email"));

        // DB에 data에서 받아온 정보를 가진 사용자가 있는지 조회
        // & 회원가입
        authService.AppleSignUp(sub, email);
    }

    // 애플 로그인 페이지
    public Map<String, String> getLoginMetaInfo() {

        Map<String, String> metaInfo = new HashMap<>();

        metaInfo.put("CLIENT_ID", clientId);
        metaInfo.put("REDIRECT_URI", REDIRECT_URL);
        metaInfo.put("NONCE", "20C20D-0S8-1K8"); // Test value

        return metaInfo;
    }

}
