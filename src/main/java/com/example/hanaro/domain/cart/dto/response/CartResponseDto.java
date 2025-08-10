package com.example.hanaro.domain.cart.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CartResponseDto {
	private List<CartItemResponseDto> items;
	private int totalPrice;
}