package igoMoney.BE.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import igoMoney.BE.domain.Challenge;

import java.time.LocalDate;
import java.util.List;

import static igoMoney.BE.domain.QChallenge.challenge;

public class ChallengeCustomRepositoryImpl implements ChallengeCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public ChallengeCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Challenge> findAllNotStarted(int pageSize, Long lastId, LocalDate date){
        return jpaQueryFactory.selectFrom(challenge)
                .where(challenge.startDate.gt(date),
                        ltChallengeId(lastId))
                .orderBy(challenge.id.desc())
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<Challenge> findAllNotStartedByCategory(int pageSize, Long lastId, LocalDate date, Integer categoryId){
        return jpaQueryFactory.selectFrom(challenge)
                .where( challenge.categoryId.eq(categoryId),
                        challenge.startDate.gt(date),
                        ltChallengeId(lastId))
                .orderBy(challenge.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression ltChallengeId(Long lastId) {

        if (lastId == null) {
            return null; // BooleanExpression 자리에 null이 반환되면 조건문에서 자동으로 제거된다
        }
        return challenge.id.lt(lastId);
    }
}
