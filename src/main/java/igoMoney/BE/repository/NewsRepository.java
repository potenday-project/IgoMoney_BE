package igoMoney.BE.repository;

import igoMoney.BE.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findAllByIdGreaterThan(Long lastId);
}
