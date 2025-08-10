package com.example.hanaro.domain.cart.service;

import com.example.hanaro.domain.cart.dto.request.CartItemRequestDto;
import com.example.hanaro.domain.cart.dto.request.CartItemUpdateRequestDto;
import com.example.hanaro.domain.cart.dto.response.CartItemResponseDto;
import com.example.hanaro.domain.cart.dto.response.CartResponseDto;
import com.example.hanaro.domain.cart.entity.Cart;
import com.example.hanaro.domain.cart.entity.CartItem;
import com.example.hanaro.domain.cart.exception.CartErrorCode;
import com.example.hanaro.domain.cart.exception.CartException;
import com.example.hanaro.domain.cart.repository.CartItemRepository;
import com.example.hanaro.domain.cart.repository.CartRepository;
import com.example.hanaro.domain.product.entity.Product;
import com.example.hanaro.domain.product.exception.ProductException;
import com.example.hanaro.domain.product.repository.ProductRepository;
import com.example.hanaro.domain.user.entity.User;
import com.example.hanaro.domain.user.exception.UserException;
import com.example.hanaro.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.hanaro.domain.product.exception.ProductErrorCode.PRODUCT_NOT_FOUND;
import static com.example.hanaro.domain.user.exception.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;

	@Override
	@Transactional
	public void addProductToCart(Long userId, CartItemRequestDto requestDto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));

		Product product = productRepository.findById(requestDto.getProductId())
			.orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

		Cart cart = cartRepository.findByUser(user)
			.orElseGet(() -> {
				Cart newCart = new Cart();
				newCart.setUser(user);
				return cartRepository.save(newCart);
			});

		Optional<CartItem> cartItemOptional = cartItemRepository.findByCartAndProduct(cart, product);

		if (cartItemOptional.isPresent()) {
			CartItem cartItem = cartItemOptional.get();
			cartItem.setQuantity(cartItem.getQuantity() + requestDto.getQuantity());
			cartItemRepository.save(cartItem);
		} else {
			CartItem newCartItem = new CartItem(cart, product, requestDto.getQuantity());
			cartItemRepository.save(newCartItem);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public CartResponseDto getCart(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));

		Cart cart = cartRepository.findByUser(user)
			.orElse(null);

		if (cart == null || cart.getCartItems().isEmpty()) {
			return CartResponseDto.builder()
				.items(Collections.emptyList())
				.totalPrice(0)
				.build();
		}

		List<CartItemResponseDto> items = cart.getCartItems().stream()
			.map(CartItemResponseDto::fromEntity)
			.collect(Collectors.toList());

		int totalPrice = items.stream()
			.mapToInt(item -> item.getPrice() * item.getQuantity())
			.sum();

		return CartResponseDto.builder()
			.items(items)
			.totalPrice(totalPrice)
			.build();
	}

	@Override
	@Transactional
	public void updateCartItemQuantity(Long userId, Long cartItemId, CartItemUpdateRequestDto requestDto) {
		CartItem cartItem = cartItemRepository.findById(cartItemId)
			.orElseThrow(() -> new CartException(CartErrorCode.CART_ITEM_NOT_FOUND));

		if (!cartItem.getCart().getUser().getId().equals(userId)) {
			throw new CartException(CartErrorCode.ACCESS_DENIED_CART_ITEM);
		}

		cartItem.setQuantity(requestDto.getQuantity());
	}

	@Override
	@Transactional
	public void deleteCartItem(Long userId, Long cartItemId) {
		CartItem cartItem = cartItemRepository.findById(cartItemId)
			.orElseThrow(() -> new CartException(CartErrorCode.CART_ITEM_NOT_FOUND));

		if (!cartItem.getCart().getUser().getId().equals(userId)) {
			throw new CartException(CartErrorCode.ACCESS_DENIED_CART_ITEM);
		}

		cartItemRepository.delete(cartItem);
	}
}