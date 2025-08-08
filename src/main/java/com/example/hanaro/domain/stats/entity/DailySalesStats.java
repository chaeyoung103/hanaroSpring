package com.example.hanaro.domain.stats.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "daily_sales_stats")
@Getter
@Setter
@NoArgsConstructor
public class DailySalesStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "stats_date", unique = true, nullable = false)
    private Date statsDate;

    @Column(name = "total_revenue", nullable = false)
    private BigDecimal totalRevenue;

    @Column(name = "total_orders", nullable = false)
    private Integer totalOrders;
}