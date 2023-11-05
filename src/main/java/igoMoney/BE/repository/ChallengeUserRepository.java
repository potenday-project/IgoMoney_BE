package igoMoney.BE.repository;

import igoMoney.BE.domain.ChallengeUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeUserRepository extends JpaRepository<ChallengeUser, Long> {

    List<ChallengeUser> findAllByChallengeId(Long challengeId);
    List<ChallengeUser> findAllByUserId(Long userId);
    ChallengeUser findByChallengeIdAndUserId(Long challengeId, Long userId);
}
