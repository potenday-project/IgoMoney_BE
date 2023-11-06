package igoMoney.BE.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChallenge is a Querydsl query type for Challenge
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChallenge extends EntityPathBase<Challenge> {

    private static final long serialVersionUID = 563322277L;

    public static final QChallenge challenge = new QChallenge("challenge");

    public final igoMoney.BE.common.entity.QBaseEntity _super = new igoMoney.BE.common.entity.QBaseEntity(this);

    public final NumberPath<Integer> categoryId = createNumber("categoryId", Integer.class);

    public final ListPath<ChallengeUser, QChallengeUser> challengeUsers = this.<ChallengeUser, QChallengeUser>createList("challengeUsers", ChallengeUser.class, QChallengeUser.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> leaderId = createNumber("leaderId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final ListPath<Record, QRecord> records = this.<Record, QRecord>createList("records", Record.class, QRecord.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final StringPath status = createString("status");

    public final NumberPath<Integer> targetAmount = createNumber("targetAmount", Integer.class);

    public final NumberPath<Integer> term = createNumber("term", Integer.class);

    public final StringPath title = createString("title");

    public final NumberPath<Long> winnerId = createNumber("winnerId", Long.class);

    public QChallenge(String variable) {
        super(Challenge.class, forVariable(variable));
    }

    public QChallenge(Path<? extends Challenge> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChallenge(PathMetadata metadata) {
        super(Challenge.class, metadata);
    }

}

