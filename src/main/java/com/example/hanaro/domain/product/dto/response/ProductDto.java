package com.example.hanaro.domain.product.dto.response;

import com.example.hanaro.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ProductDto {

	private final Long productId;
	private final String name;
	private final int price;
	private final String description;
	private final int stockQuantity;
	private final List<ProductImageDto> images;

	public static ProductDto fromEntity(Product product) {
		return ProductDto.builder()
			.productId(product.getId())
			.name(product.getName())
			.price(product.getPrice())
			.description(product.getDescription())
			.stockQuantity(product.getStockQuantity())
			.images(product.getProductImages().stream()
				.map(ProductImageDto::new)
				.collect(Collectors.toList()))
			.build();
	}
}