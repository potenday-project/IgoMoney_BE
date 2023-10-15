package igoMoney.BE.service;

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
}
