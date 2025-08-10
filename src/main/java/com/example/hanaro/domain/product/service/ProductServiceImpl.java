package com.example.hanaro.domain.product.service;

import com.example.hanaro.domain.product.dto.request.ProductCreateRequestDto;
import com.example.hanaro.domain.product.dto.response.ProductDto;
import com.example.hanaro.domain.product.entity.Product;
import com.example.hanaro.domain.product.entity.ProductImage;
import com.example.hanaro.domain.product.exception.ProductException;
import com.example.hanaro.domain.product.repository.ProductRepository;
import com.example.hanaro.global.util.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.hanaro.domain.product.exception.ProductErrorCode.DUPLICATE_PRODUCT_NAME;
import static com.example.hanaro.domain.product.exception.ProductErrorCode.FILE_UPLOAD_FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final FileUploadService fileUploadService;

	@Override
	@Transactional
	public void createProduct(ProductCreateRequestDto requestDto) {
		if (productRepository.findByName(requestDto.getName()).isPresent()) {
			throw new ProductException(DUPLICATE_PRODUCT_NAME);
		}

		Product product = Product.builder()
			.name(requestDto.getName())
			.price(requestDto.getPrice())
			.description(requestDto.getDescription())
			.stockQuantity(requestDto.getStockQuantity())
			.build();

		List<MultipartFile> images = requestDto.getImages();
		if (images != null && !images.isEmpty()) {
			try {
				List<ProductImage> uploadedImages = fileUploadService.uploadImages(product, images);
				product.getProductImages().addAll(uploadedImages);
			} catch (IOException e) {
				log.error("Image upload failed", e);
				throw new ProductException(FILE_UPLOAD_FAILED);
			}
		}

		productRepository.save(product);
		log.info("새로운 상품이 등록되었습니다: {}", product.getName());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> findProducts(Authentication authentication) {
		log.info("전체 상품 목록을 조회합니다.");
		return productRepository.findAll().stream()
			.map(ProductDto::fromEntity)
			.collect(Collectors.toList());
	}
}