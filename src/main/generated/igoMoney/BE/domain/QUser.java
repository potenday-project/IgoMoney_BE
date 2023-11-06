package igoMoney.BE.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -738555575L;

    public static final QUser user = new QUser("user");

    public final NumberPath<Integer> badgeCount = createNumber("badgeCount", Integer.class);

    public final BooleanPath banned = createBoolean("banned");

    public final DatePath<java.time.LocalDate> banReleaseDate = createDate("banReleaseDate", java.time.LocalDate.class);

    public final NumberPath<Integer> challengeCount = createNumber("challengeCount", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image = createString("image");

    public final BooleanPath inChallenge = createBoolean("inChallenge");

    public final StringPath loginId = createString("loginId");

    public final NumberPath<Long> myChallengeId = createNumber("myChallengeId", Long.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final StringPath provider = createString("provider");

    public final NumberPath<Integer> reportedCount = createNumber("reportedCount", Integer.class);

    public final StringPath role = createString("role");

    public final NumberPath<Integer> winCount = createNumber("winCount", Integer.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

