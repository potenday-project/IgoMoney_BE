package igoMoney.BE.repository;

import igoMoney.BE.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    boolean existsByKeyLoginId(String loginId);

    void deleteByKeyLoginId(String loginId);
    Optional<RefreshToken> findByKeyLoginId(String loginId);

}
