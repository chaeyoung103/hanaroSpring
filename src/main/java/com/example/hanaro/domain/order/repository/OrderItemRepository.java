package com.example.hanaro.domain.order.repository;

import com.example.hanaro.domain.order.entity.OrderItem;
import com.example.hanaro.domain.product.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	boolean existsByProduct(Product product);
}