package com.example.hanaro.domain.product.entity;

import com.example.hanaro.global.entity.BaseEntity;
import jakarta.persistence.*; // jakarta.persistence.* 로 import
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProductImage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String imageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	public ProductImage(String imageUrl, Product product) {
		this.imageUrl = imageUrl;
		this.product = product;
	}
}