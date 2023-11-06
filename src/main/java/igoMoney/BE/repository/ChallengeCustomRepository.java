package igoMoney.BE.repository;

import igoMoney.BE.domain.Challenge;

import java.time.LocalDate;
import java.util.List;

public interface ChallengeCustomRepository {
    List<Challenge> findAllNotStarted(int pageSize, Long lastId, LocalDate date);
}
