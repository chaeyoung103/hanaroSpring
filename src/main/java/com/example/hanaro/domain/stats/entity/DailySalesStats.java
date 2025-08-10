package com.example.hanaro.domain.stats.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

import com.example.hanaro.global.entity.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DailySalesStats extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "stats_date", unique = true, nullable = false)
    private Date statsDate;

    @Column(name = "total_revenue", nullable = false)
    private int totalRevenue;

    @Column(name = "total_orders", nullable = false)
    private int totalOrders;
}