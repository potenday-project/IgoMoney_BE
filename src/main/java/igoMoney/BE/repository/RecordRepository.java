package igoMoney.BE.repository;

import igoMoney.BE.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findAllByUserIdAndDate(Long userId, LocalDate date);

    @Query(value="SELECT r.user_id, SUM(r.cost) FROM record r WHERE r.challenge_id= :challengeId GROUP BY r.user_id", nativeQuery = true)
    List<Object[]> calculateTotalCostByUserId(@Param("challengeId") Long challengeId);

}
