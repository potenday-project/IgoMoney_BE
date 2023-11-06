package igoMoney.BE.repository;

import igoMoney.BE.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long>, ChallengeCustomRepository {

    @Query("select c from Challenge c where c.startDate > :date")
    List<Challenge> findAllByStartDateIsAfter(@Param("date") LocalDate date);
    List<Challenge> findAllByStatus(String status);
}
