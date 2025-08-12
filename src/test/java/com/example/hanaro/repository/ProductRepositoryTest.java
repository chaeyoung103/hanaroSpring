package com.example.hanaro.domain.product.repository;

import com.example.hanaro.domain.product.entity.Product;
import com.example.hanaro.domain.product.entity.ProductImage;
import com.example.hanaro.repository.RepositoryTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductRepositoryTest extends RepositoryTest {

	@Autowired
	private ProductRepository productRepository;


}