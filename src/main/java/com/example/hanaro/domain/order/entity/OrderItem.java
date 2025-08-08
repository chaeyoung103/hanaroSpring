package com.example.hanaro.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import com.example.hanaro.domain.product.entity.Product;
import com.example.hanaro.global.entity.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product", nullable = false)
    private Product product;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;
}