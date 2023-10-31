package igoMoney.BE.repository;

import igoMoney.BE.domain.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
}
