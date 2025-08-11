package com.example.hanaro.domain.stats.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyProductStats is a Querydsl query type for DailyProductStats
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyProductStats extends EntityPathBase<DailyProductStats> {

    private static final long serialVersionUID = 1986103011L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyProductStats dailyProductStats = new QDailyProductStats("dailyProductStats");

    public final com.example.hanaro.global.entity.QBaseEntity _super = new com.example.hanaro.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.example.hanaro.domain.product.entity.QProduct product;

    public final DatePath<java.sql.Date> statsDate = createDate("statsDate", java.sql.Date.class);

    public final NumberPath<Integer> totalQuantitySold = createNumber("totalQuantitySold", Integer.class);

    public final NumberPath<Integer> totalRevenue = createNumber("totalRevenue", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QDailyProductStats(String variable) {
        this(DailyProductStats.class, forVariable(variable), INITS);
    }

    public QDailyProductStats(Path<? extends DailyProductStats> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyProductStats(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyProductStats(PathMetadata metadata, PathInits inits) {
        this(DailyProductStats.class, metadata, inits);
    }

    public QDailyProductStats(Class<? extends DailyProductStats> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new com.example.hanaro.domain.product.entity.QProduct(forProperty("product")) : null;
    }

}

