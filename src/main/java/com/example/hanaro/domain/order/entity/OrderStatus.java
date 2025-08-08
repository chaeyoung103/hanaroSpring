package com.example.hanaro.domain.order.entity;

public enum OrderStatus {
	// 1. 주문 상태 값들을 Enum 상수로 정의
	PAYED("결제완료"),
	PREPARING("배송준비"),
	TRANSITING("배송중"),
	DELIVERED("배송완료");

	private final String description;

	OrderStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	public OrderStatus getNextState() {
		return switch (this) {
			case PAYED -> PREPARING;
			case PREPARING -> TRANSITING;
			case TRANSITING -> DELIVERED;
			case DELIVERED -> DELIVERED;
		};
	}
}