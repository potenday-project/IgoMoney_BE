package igoMoney.BE.repository;

import igoMoney.BE.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findAllByUserIdAndDate(Long userId, LocalDate date);
}
