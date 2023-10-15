package igoMoney.BE.repository;

import igoMoney.BE.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    boolean existsByUserId(Long userId);

    void deleteByUserId(Long userId);
    Optional<RefreshToken> findByUserId(Long userId);

}
