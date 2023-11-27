package igoMoney.BE.repository;

import igoMoney.BE.domain.Challenge;

import java.time.LocalDate;
import java.util.List;

public interface ChallengeCustomRepository {
    List<Challenge> findAllNotStarted(int pageSize, Long lastId, LocalDate date);
    List<Challenge> findAllNotStartedByCategory(int pageSize, Long lastId, LocalDate date, Integer categoryId);
    List<Challenge> findAllNotStartedByTargetAmount(int pageSize, Long lastId, LocalDate date, Integer targetAmount);
    List<Challenge> findAllNotStartedByCategoryAndTargetAmount(int pageSize, Long lastId, LocalDate date, Integer categoryId, Integer targetAmount);
}
