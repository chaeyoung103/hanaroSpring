package com.example.hanaro.domain.order.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.example.hanaro.domain.order.entity.Order;
import com.example.hanaro.domain.order.entity.OrderStatus;
import com.example.hanaro.domain.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long>, QuerydslPredicateExecutor<Order> {

	List<Order> findAllByUserOrderByOrderDateDesc(User user);

	/**
	 * 특정 상태의 주문들 중, 주어진 시간 이전에 마지막으로 업데이트된 주문들의 상태를 다음 상태로 변경
	 * @param currentStatus 현재 상태
	 * @param nextStatus    변경할 다음 상태
	 * @param thresholdTime 기준 시간 (이 시간 이전의 주문만 변경)
	 * @return 변경된 row의 개수
	 */
	@Modifying
	@Query("UPDATE Order o SET o.status = :nextStatus WHERE o.status = :currentStatus AND o.updatedAt < :thresholdTime")
	int updateOrderStatusByStatusAndDate(
		@Param("currentStatus") OrderStatus currentStatus,
		@Param("nextStatus") OrderStatus nextStatus,
		@Param("thresholdTime") LocalDateTime thresholdTime
	);
}