package com.example.hanaro.domain.cart.repository;

import com.example.hanaro.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}