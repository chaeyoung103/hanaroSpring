package com.example.hanaro.domain.stats.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

import com.example.hanaro.domain.product.entity.Product;

@Entity
@Table(name = "daily_product_stats")
@Getter
@Setter
@NoArgsConstructor
public class DailyProductStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "stats_date", nullable = false)
    private Date statsDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "total_quantity_sold", nullable = false)
    private Integer totalQuantitySold;

    @Column(name = "total_revenue", nullable = false)
    private BigDecimal totalRevenue;
}