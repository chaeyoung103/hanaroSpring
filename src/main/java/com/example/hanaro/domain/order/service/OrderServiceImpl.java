package com.example.hanaro.domain.order.service;


import static com.example.hanaro.domain.user.exception.UserErrorCode.*;

import com.example.hanaro.domain.cart.entity.Cart;
import com.example.hanaro.domain.cart.entity.CartItem;
import com.example.hanaro.domain.cart.repository.CartRepository;
import com.example.hanaro.domain.order.dto.request.OrderSearchRequestDto;
import com.example.hanaro.domain.order.dto.response.OrderCreateResponseDto;
import com.example.hanaro.domain.order.dto.response.OrderResponseDto;
import com.example.hanaro.domain.order.entity.Order;
import com.example.hanaro.domain.order.entity.OrderItem;
import com.example.hanaro.domain.order.entity.OrderStatus;
import com.example.hanaro.domain.order.entity.QOrder;
import com.example.hanaro.domain.order.exception.OrderErrorCode;
import com.example.hanaro.domain.order.exception.OrderException;
import com.example.hanaro.domain.order.repository.OrderRepository;
import com.example.hanaro.domain.product.entity.Product;
import com.example.hanaro.domain.user.entity.User;
import com.example.hanaro.domain.user.exception.UserException;
import com.example.hanaro.domain.user.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final CartRepository cartRepository;

	@Override
	@Transactional
	public OrderCreateResponseDto createOrder(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));

		Cart cart = cartRepository.findByUser(user)
			.orElseThrow(() -> new OrderException(OrderErrorCode.CART_NOT_FOUND));

		if (cart.getCartItems().isEmpty()) {
			throw new OrderException(OrderErrorCode.CART_IS_EMPTY);
		}

		Order order = new Order();
		order.setUser(user);
		order.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));
		order.setStatus(OrderStatus.PAYED);

		int totalPrice = 0;
		for (CartItem cartItem : cart.getCartItems()) {
			Product product = cartItem.getProduct();

			if (product.getStockQuantity() < cartItem.getQuantity()) {
				throw new OrderException(OrderErrorCode.INSUFFICIENT_STOCK, product.getName());
			}

			product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());

			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setProduct(product);
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setPrice(product.getPrice());

			order.getOrderItems().add(orderItem);
			totalPrice += orderItem.getPrice() * orderItem.getQuantity();
		}
		order.setTotalPrice(totalPrice);

		Order savedOrder = orderRepository.save(order);

		cart.getCartItems().clear();
		cartRepository.save(cart);

		return OrderCreateResponseDto.builder()
			.orderId(savedOrder.getId())
			.build();
	}


	@Override
	@Transactional(readOnly = true)
	public List<OrderResponseDto> findOrders(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));

		List<Order> orders = orderRepository.findAllByUserOrderByOrderDateDesc(user);

		return orders.stream()
			.map(OrderResponseDto::fromEntity)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	// [✨수정✨] Pageable 파라미터 삭제, 반환 타입을 Page에서 List로 변경
	public List<OrderResponseDto> searchOrders(OrderSearchRequestDto searchDto) {
		QOrder order = QOrder.order;
		BooleanBuilder builder = new BooleanBuilder();

		if (StringUtils.hasText(searchDto.getProductName())) {
			builder.and(order.orderItems.any().product.name.containsIgnoreCase(searchDto.getProductName()));
		}

		if (searchDto.getOrderStatus() != null) {
			builder.and(order.status.eq(searchDto.getOrderStatus()));
		}

		Sort sort = Sort.by(Sort.Direction.DESC, "orderDate");

		return ((List<Order>) orderRepository.findAll(builder, sort))
			.stream()
			.map(OrderResponseDto::fromEntity)
			.collect(Collectors.toList());
	}
}