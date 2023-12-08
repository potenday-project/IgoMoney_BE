package igoMoney.BE.repository;

import igoMoney.BE.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long>, ChallengeCustomRepository {

    List<Challenge> findAllByStartDateAndStatus(LocalDate date, String status);
    List<Challenge> findAllByStatus(String status);
    List<Challenge> findAllByEndDateAndStatus(LocalDate date, String status);
}
