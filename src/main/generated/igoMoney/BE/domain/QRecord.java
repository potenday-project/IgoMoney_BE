package igoMoney.BE.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecord is a Querydsl query type for Record
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecord extends EntityPathBase<Record> {

    private static final long serialVersionUID = -1181179313L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecord record = new QRecord("record");

    public final igoMoney.BE.common.entity.QBaseEntity _super = new igoMoney.BE.common.entity.QBaseEntity(this);

    public final QChallenge challenge;

    public final StringPath content = createString("content");

    public final NumberPath<Integer> cost = createNumber("cost", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final BooleanPath hide = createBoolean("hide");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<Image, QImage> image = this.<Image, QImage>createList("image", Image.class, QImage.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath title = createString("title");

    public final QUser user;

    public QRecord(String variable) {
        this(Record.class, forVariable(variable), INITS);
    }

    public QRecord(Path<? extends Record> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecord(PathMetadata metadata, PathInits inits) {
        this(Record.class, metadata, inits);
    }

    public QRecord(Class<? extends Record> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.challenge = inits.isInitialized("challenge") ? new QChallenge(forProperty("challenge")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

