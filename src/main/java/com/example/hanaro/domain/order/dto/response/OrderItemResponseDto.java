package com.example.hanaro.domain.order.dto.response;

import com.example.hanaro.domain.order.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemResponseDto {
	private final String productName;
	private final int quantity;
	private final int price;

	public static OrderItemResponseDto fromEntity(OrderItem orderItem) {
		return OrderItemResponseDto.builder()
			.productName(orderItem.getProduct().getName())
			.quantity(orderItem.getQuantity())
			.price(orderItem.getPrice())
			.build();
	}
}