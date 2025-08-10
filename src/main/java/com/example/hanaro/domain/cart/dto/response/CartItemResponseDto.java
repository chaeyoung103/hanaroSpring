package com.example.hanaro.domain.cart.dto.response;

import com.example.hanaro.domain.cart.entity.CartItem;
import lombok.Builder; // Builder import
import lombok.Getter;

@Getter
@Builder
public class CartItemResponseDto {
	private final Long cartItemId;
	private final Long productId;
	private final String productName;
	private final int price;
	private final int quantity;
	private final String imageUrl;

	public static CartItemResponseDto fromEntity(CartItem cartItem) {
		return CartItemResponseDto.builder()
			.cartItemId(cartItem.getId())
			.productId(cartItem.getProduct().getId())
			.productName(cartItem.getProduct().getName())
			.price(cartItem.getProduct().getPrice())
			.quantity(cartItem.getQuantity())
			.imageUrl(cartItem.getProduct().getProductImages().isEmpty()
				? null
				: cartItem.getProduct().getProductImages().get(0).getImageUrl())
			.build();
	}
}