package com.example.hanaro.domain.order.service;

import com.example.hanaro.domain.order.entity.OrderStatus;
import com.example.hanaro.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSchedulingService {

	private final OrderRepository orderRepository;

	//  결제완료 -> 배송준비 : 5분 주기
	@Scheduled(cron = "0 */5 * * * *")
	@Transactional
	public void updatePaidToPreparing() {
		log.info("결제완료->배송준비 스케줄러 실행");
		updateStatusFrom(OrderStatus.PAYED);
	}

	// 배송준비 -> 배송중 : 15분 주기
	@Scheduled(cron = "0 */15 * * * *")
	@Transactional
	public void updatePreparingToTransiting() {
		log.info("배송준비->배송중 스케줄러 실행");
		updateStatusFrom(OrderStatus.PREPARING);
	}

	// 배송중 -> 배송완료 : 1시간 주기
	@Scheduled(cron = "0 0 * * * *")
	@Transactional
	public void updateTransitingToDelivered() {
		log.info("배송중->배송완료 스케줄러 실행");
		updateStatusFrom(OrderStatus.TRANSITING);
	}

	private void updateStatusFrom(OrderStatus currentStatus) {
		if (currentStatus.getIntervalMinutes() < 0) {
			return;
		}

		OrderStatus nextStatus = currentStatus.getNextState();
		LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(currentStatus.getIntervalMinutes());

		int updatedCount = orderRepository.updateOrderStatusByStatusAndDate(
			currentStatus,
			nextStatus,
			thresholdTime
		);

		if (updatedCount > 0) {
			log.info("[주문 상태 변경] {} -> {}: 총 {}건 처리", currentStatus.getDescription(), nextStatus.getDescription(), updatedCount);
		}
	}
}