package com.example.hanaro.domain.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProductCreateRequestDto {

	@NotBlank(message = "상품명은 필수 입력 값입니다.")
	private String name;

	@NotNull(message = "가격은 필수 입력 값입니다.")
	@Min(value = 0, message = "가격은 0 이상이어야 합니다.")
	private int price;

	private String description;

	@NotNull(message = "재고는 필수 입력 값입니다.")
	@Min(value = 1, message = "재고는 1 이상이어야 합니다.")
	private int stockQuantity;

	private List<MultipartFile> images;
}