package igoMoney.BE.service;

import igoMoney.BE.common.exception.CustomException;
import igoMoney.BE.common.exception.ErrorCode;
import igoMoney.BE.common.jwt.dto.TokenDto;
import igoMoney.BE.domain.RefreshToken;
import igoMoney.BE.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(TokenDto tokenDto) {

        Long userId = tokenDto.getUserId();
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .refreshToken(tokenDto.getRefreshToken())
                .build();

        if(refreshTokenRepository.existsByUserId(userId)) {
            refreshTokenRepository.deleteByUserId(userId);
        }
        refreshTokenRepository.save(refreshToken);
    }

    public void checkRefreshToken(Long userId, String token){
        RefreshToken token2 = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_INVALID));
        if(!token2.getRefreshToken().equals(token)){
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }
    }
}
