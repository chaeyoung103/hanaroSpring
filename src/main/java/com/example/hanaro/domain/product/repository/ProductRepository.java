package com.example.hanaro.domain.product.repository;

import java.util.List;
import java.util.Optional;

import com.example.hanaro.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findByName(String name);
	List<Product> findByNameContainingIgnoreCase(String keyword);
}