package com.example.hanaro.domain.product.repository;

import com.example.hanaro.domain.product.entity.Product;
import com.example.hanaro.domain.product.entity.ProductImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // [✨추가✨]

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTest {

	@Autowired
	private ProductRepository productRepository;

	@Test
	@DisplayName("상품 더미 데이터 50개 생성")
	@Rollback(false)
	void createDummyProducts() {
		String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

		for (int i = 1; i <= 50; i++) {
			Product product = Product.builder()
				.name("테스트 상품 " + i)
				.price(1000 * i)
				.description("이것은 테스트 상품 " + i + "에 대한 설명입니다.")
				.stockQuantity(100)
				.build();

			List<ProductImage> images = new ArrayList<>();
			String uniqueName1 = UUID.randomUUID().toString() + ".jpg";
			String uniqueName2 = UUID.randomUUID().toString() + ".jpg";

			String imageUrl1 = "/upload/" + datePath + "/" + uniqueName1;
			String imageUrl2 = "/upload/" + datePath + "/" + uniqueName2;

			images.add(new ProductImage(imageUrl1, product));
			images.add(new ProductImage(imageUrl2, product));

			product.setProductImages(images);

			productRepository.save(product);
		}
	}
}