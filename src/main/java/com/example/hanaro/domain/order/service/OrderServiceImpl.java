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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final CartRepository cartRepository;

	// 사용자 - 주문 생성
	@Override
	@Transactional
	public OrderCreateResponseDto createOrder(Long userId) {
		log.info("======== 주문 생성 시작 (사용자 ID: {}) ========", userId);
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));

		Cart cart = cartRepository.findByUser(user)
			.orElseThrow(() -> new OrderException(OrderErrorCode.CART_NOT_FOUND));

		if (cart.getCartItems().isEmpty()) {
			log.warn(" >> 주문 생성 실패: 장바구니가 비어있습니다. (사용자 ID: {})", userId);
			throw new OrderException(OrderErrorCode.CART_IS_EMPTY);
		}

		// 주문 엔티티 생성 및 초기화
		Order order = new Order();
		order.setUser(user);
		order.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));
		order.setStatus(OrderStatus.PAYED);

		int totalPrice = 0;
		// 장바구니 상품을 주문 상품으로 변환 및 재고 차감
		for (CartItem cartItem : cart.getCartItems()) {
			Product product = cartItem.getProduct();

			// 재고 확인
			if (product.getStockQuantity() < cartItem.getQuantity()) {
				log.error(" >> 주문 생성 실패: 재고 부족 (상품명: {})", product.getName());
				throw new OrderException(OrderErrorCode.INSUFFICIENT_STOCK, product.getName());
			}

			// 재고 차감
			int newStock = product.getStockQuantity() - cartItem.getQuantity();
			product.setStockQuantity(newStock);
			log.info("  - 상품: {}, 수량: {}, 재고 차감 후: {}", product.getName(), cartItem.getQuantity(), newStock);

			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setProduct(product);
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setPrice(product.getPrice()); // 주문 시점의 가격 기록

			order.getOrderItems().add(orderItem);
			totalPrice += orderItem.getPrice() * orderItem.getQuantity();
		}
		order.setTotalPrice(totalPrice);

		Order savedOrder = orderRepository.save(order);

		// 주문 완료 후 장바구니 비우기
		log.info("  >> 장바구니 비우기 시작 (사용자 ID: {})", userId);
		cart.getCartItems().clear();
		cartRepository.save(cart);
		log.info("  >> 장바구니 비우기 완료");

		log.info("======== 주문 생성 성공 (주문 ID: {}) ========", savedOrder.getId());
		return OrderCreateResponseDto.builder()
			.orderId(savedOrder.getId())
			.build();
	}

	// 사용자 - 주문 내역 조회
	@Override
	@Transactional(readOnly = true)
	public List<OrderResponseDto> findOrders(Long userId) {
		log.info("사용자 주문 내역 조회 (사용자 ID: {})", userId);
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));

		List<Order> orders = orderRepository.findAllByUserOrderByOrderDateDesc(user);

		return orders.stream()
			.map(OrderResponseDto::fromEntity)
			.collect(Collectors.toList());
	}

	// 관리자 - 주문 내역 검색
	@Override
	@Transactional(readOnly = true)
	public List<OrderResponseDto> searchOrders(OrderSearchRequestDto searchDto) {
		log.info("관리자 주문 내역 검색. 조건: 상품명='{}', 주문상태='{}'", searchDto.getProductName(), searchDto.getOrderStatus());
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