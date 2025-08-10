package com.example.hanaro.domain.product.dto.response;

import com.example.hanaro.domain.product.entity.ProductImage;
import lombok.Getter;

@Getter
public class ProductImageDto {
	private final Long imageId;
	private final String imageUrl;

	public ProductImageDto(ProductImage productImage) {
		this.imageId = productImage.getId();
		this.imageUrl = productImage.getImageUrl();
	}
}