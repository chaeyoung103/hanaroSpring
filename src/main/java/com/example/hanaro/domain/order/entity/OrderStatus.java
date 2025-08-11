package com.example.hanaro.domain.order.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
	PAYED("결제완료", 5),          // 5분 뒤 '배송준비'로 변경
	PREPARING("배송준비", 15),       // 15분 뒤 '배송중'으로 변경
	TRANSITING("배송중", 60),      // 60분(1시간) 뒤 '배송완료'로 변경
	DELIVERED("배송완료", -1); // 마지막 상태는 변경되지 않음 (-1)

	private final String description;
	private final int intervalMinutes;

	OrderStatus(String description, int intervalMinutes) {
		this.description = description;
		this.intervalMinutes = intervalMinutes;
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