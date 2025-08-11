package com.example.hanaro.domain.order.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.hanaro.domain.order.dto.request.OrderSearchRequestDto;
import com.example.hanaro.domain.order.dto.response.OrderCreateResponseDto;
import com.example.hanaro.domain.order.dto.response.OrderResponseDto;

public interface OrderService {
	/**
	 * 장바구니 기반으로 주문 생성
	 * @param userId 현재 로그인한 사용자의 ID
	 * @return 생성된 주문 정보 (orderId)
	 */
	OrderCreateResponseDto createOrder(Long userId);

	/**
	 * 내 주문 내역 조회
	 * @param userId 현재 로그인한 사용자의 ID
	 * @return 주문 내역 목록
	 */
	List<OrderResponseDto> findOrders(Long userId);

	/**
	 * 주문 내역 검색 (관리자용)
	 * @param searchDto 검색 조건
	 * @return 검색된 주문 내역 리스트 (최신순)
	 */
	List<OrderResponseDto> searchOrders(OrderSearchRequestDto searchDto);
}