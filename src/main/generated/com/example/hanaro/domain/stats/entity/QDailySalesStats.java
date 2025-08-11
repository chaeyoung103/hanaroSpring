package com.example.hanaro.domain.stats.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDailySalesStats is a Querydsl query type for DailySalesStats
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailySalesStats extends EntityPathBase<DailySalesStats> {

    private static final long serialVersionUID = -575343194L;

    public static final QDailySalesStats dailySalesStats = new QDailySalesStats("dailySalesStats");

    public final com.example.hanaro.global.entity.QBaseEntity _super = new com.example.hanaro.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.sql.Date> statsDate = createDate("statsDate", java.sql.Date.class);

    public final NumberPath<Integer> totalOrders = createNumber("totalOrders", Integer.class);

    public final NumberPath<Integer> totalRevenue = createNumber("totalRevenue", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QDailySalesStats(String variable) {
        super(DailySalesStats.class, forVariable(variable));
    }

    public QDailySalesStats(Path<? extends DailySalesStats> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDailySalesStats(PathMetadata metadata) {
        super(DailySalesStats.class, metadata);
    }

}

