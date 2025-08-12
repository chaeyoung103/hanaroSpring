package com.example.hanaro.domain.product.entity;

import com.example.hanaro.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor; // [추가]
import lombok.Builder; // [추가]
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int price;

    @Lob
    private String description;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductImage> productImages = new ArrayList<>();

    public void addImage(ProductImage productImage) {
        productImages.add(productImage);
        productImage.setProduct(this);
    }
}