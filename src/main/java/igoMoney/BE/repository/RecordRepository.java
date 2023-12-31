package igoMoney.BE.repository;

import igoMoney.BE.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findAllByUserIdAndDate(Long userId, LocalDate date);

    @Query(value="SELECT r.user_id, SUM(r.cost) FROM record r WHERE r.challenge_id= :challengeId AND r.user_id= :userId", nativeQuery = true)
    List<Object[]> calculateTotalCostByUserId(@Param("challengeId") Long challengeId, @Param("userId")Long userId);

    @Query(value="SELECT r.user_id, SUM(r.cost) FROM record r WHERE r.challenge_id= :challengeId GROUP BY r.user_id", nativeQuery = true)
    List<Object[]> calculateTotalCostByChallengeId(@Param("challengeId") Long challengeId);

    List<Record> findAllByUserId(Long userId);
    @Query(value="SELECT COUNT(*) FROM record r WHERE r.user_id= :userId AND r.date >= DATE_SUB(:date,INTERVAL 3 DAY)", nativeQuery = true)
    Integer countByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    Boolean existsByUserIdAndDate(Long userId, LocalDate date);
}
