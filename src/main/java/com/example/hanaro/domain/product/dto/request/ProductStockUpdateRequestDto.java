package com.example.hanaro.domain.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductStockUpdateRequestDto {

	@NotNull(message = "재고는 필수 입력 값입니다.")
	@Min(value = 0, message = "재고는 0 이상이어야 합니다.")
	private Integer stockQuantity;
}