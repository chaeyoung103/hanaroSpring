package com.example.hanaro.repository;

import com.example.hanaro.domain.product.entity.Product;
import com.example.hanaro.domain.product.entity.ProductImage;
import com.example.hanaro.domain.product.repository.ProductImageRepository;
import com.example.hanaro.domain.product.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class ProductImageRepositoryTest extends RepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        productImageRepository.deleteAll();
        productRepository.deleteAll();

        testProduct = Product.builder()
                .name("Sample Product")
                .price(5000)
                .stockQuantity(20)
                .build();
        productRepository.save(testProduct);
    }

    @Test
    @Order(1)
    void saveAndRetrieveImage() {
        ProductImage image = new ProductImage("http://example.com/image.png", testProduct);
        productImageRepository.save(image);

        Optional<ProductImage> found = productImageRepository.findById(image.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getImageUrl()).isEqualTo("http://example.com/image.png");
        assertThat(found.get().getProduct().getName()).isEqualTo("Sample Product");
    }
}