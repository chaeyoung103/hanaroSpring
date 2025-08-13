package com.example.hanaro.domain.order.dto.response;


import com.example.hanaro.domain.order.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class OrderResponseDto {
	private final Long orderId;
	private final Timestamp orderDate;
	private final String status;
	private final int totalPrice;
	private final List<OrderItemResponseDto> orderItems;
	private final Long userId;

	public static OrderResponseDto fromEntity(Order order) {
		return OrderResponseDto.builder()
			.userId(order.getUser().getId())
			.orderId(order.getId())
			.orderDate(order.getOrderDate())
			.status(order.getStatus().getDescription())
			.totalPrice(order.getTotalPrice())
			.orderItems(order.getOrderItems().stream()
				.map(OrderItemResponseDto::fromEntity)
				.collect(Collectors.toList()))
			.build();
	}
}