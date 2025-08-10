package com.example.hanaro.domain.product.entity;

import com.example.hanaro.global.entity.BaseEntity;
import jakarta.persistence.*; // jakarta.persistence.* 로 import
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList; // ArrayList import 추가
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Lob
    private String description;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductImage> productImages = new ArrayList<>();
}