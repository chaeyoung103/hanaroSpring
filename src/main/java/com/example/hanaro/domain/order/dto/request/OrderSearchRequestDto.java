package com.example.hanaro.domain.order.dto.request;

import com.example.hanaro.domain.order.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearchRequestDto {
	private String productName;
	private OrderStatus orderStatus;
}