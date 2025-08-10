package com.example.hanaro.domain.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hanaro.domain.cart.entity.Cart;
import com.example.hanaro.domain.user.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
	/**
	 * 사용자 엔티티로 장바구니를 조회
	 * @param user 조회할 사용자
	 * @return 해당 사용자의 장바구니 (Optional)
	 */
	Optional<Cart> findByUser(User user);
}