package com.example.hanaro.domain.order.repository;

import java.util.List;

import com.example.hanaro.domain.order.entity.Order;
import com.example.hanaro.domain.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>, QuerydslPredicateExecutor<Order> {

	List<Order> findAllByUserOrderByOrderDateDesc(User user);
}