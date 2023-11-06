package igoMoney.BE.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserReport is a Querydsl query type for UserReport
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserReport extends EntityPathBase<UserReport> {

    private static final long serialVersionUID = 1300754397L;

    public static final QUserReport userReport = new QUserReport("userReport");

    public final igoMoney.BE.common.entity.QBaseEntity _super = new igoMoney.BE.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final NumberPath<Long> offenderId = createNumber("offenderId", Long.class);

    public final NumberPath<Integer> reason = createNumber("reason", Integer.class);

    public final NumberPath<Long> recordId = createNumber("recordId", Long.class);

    public final NumberPath<Long> reporterId = createNumber("reporterId", Long.class);

    public QUserReport(String variable) {
        super(UserReport.class, forVariable(variable));
    }

    public QUserReport(Path<? extends UserReport> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserReport(PathMetadata metadata) {
        super(UserReport.class, metadata);
    }

}

