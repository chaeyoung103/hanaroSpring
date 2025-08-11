package com.example.hanaro.domain.product.service;

import com.example.hanaro.domain.order.repository.OrderItemRepository;
import com.example.hanaro.domain.product.dto.request.ProductCreateRequestDto;
import com.example.hanaro.domain.product.dto.request.ProductStockUpdateRequestDto;
import com.example.hanaro.domain.product.dto.response.ProductDto;
import com.example.hanaro.domain.product.entity.Product;
import com.example.hanaro.domain.product.entity.ProductImage;
import com.example.hanaro.domain.product.exception.ProductErrorCode;
import com.example.hanaro.domain.product.exception.ProductException;
import com.example.hanaro.domain.product.repository.ProductImageRepository;
import com.example.hanaro.domain.product.repository.ProductRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.hanaro.domain.product.exception.ProductErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final ProductImageRepository productImageRepository;
	private final OrderItemRepository orderItemRepository;

	@Value("${file.upload-dir}")
	private String uploadDir;

	private final long MAX_FILE_SIZE = 512 * 1024; // 512KB
	private final long MAX_TOTAL_SIZE = 3 * 1024 * 1024; // 3MB

	@Override
	@Transactional
	public void createProduct(ProductCreateRequestDto requestDto) {
		// 상품명 중복 검사
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
			validateFiles(images); // 파일 유효성 검사
			String datePath = getDatePath();

			for (MultipartFile multipartFile : images) {
				if (multipartFile.isEmpty()) continue;

				try {
					String uniqueFileName = generateUniqueFileName(multipartFile.getOriginalFilename());
					saveFile(multipartFile, datePath, uniqueFileName);

					String relativePath = "/upload/" + datePath + "/" + uniqueFileName;

					ProductImage productImage = new ProductImage();
					productImage.setImageUrl(relativePath);

					product.addImage(productImage);

				} catch (IOException e) {
					log.error("Image upload failed", e);
					throw new ProductException(FILE_UPLOAD_FAILED);
				}
			}
		}
		productRepository.save(product);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> findAllProducts() {
		return productRepository.findAll().stream()
			.map(ProductDto::fromEntity)
			.collect(Collectors.toList());
	}
	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> searchProducts(String keyword) {
		List<Product> products = (keyword == null || keyword.isBlank())
			? productRepository.findAll()
			: productRepository.findByNameContainingIgnoreCase(keyword);

		return products.stream()
			.map(ProductDto::fromEntity)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public ProductDto findProductById(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

		return ProductDto.fromEntity(product);
	}

	@Override
	@Transactional
	public void updateProduct(Long productId, ProductCreateRequestDto requestDto) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));
		product.setName(requestDto.getName());
		product.setPrice(requestDto.getPrice());
		product.setDescription(requestDto.getDescription());
		product.setStockQuantity(requestDto.getStockQuantity());

		// 새로운 이미지가 제공된 경우에만 처리
		List<MultipartFile> newImages = requestDto.getImages();
		if (newImages != null && !newImages.isEmpty()) {
			// 기존 이미지 DB에서 삭제
			productImageRepository.deleteAll(product.getProductImages());
			product.getProductImages().clear();
			// 새로운 이미지 추가
			String datePath = getDatePath();
			for (MultipartFile multipartFile : newImages) {
				if (multipartFile.isEmpty()) continue;
				try {
					String uniqueFileName = generateUniqueFileName(multipartFile.getOriginalFilename());
					saveFile(multipartFile, datePath, uniqueFileName);
					String relativePath = "/upload/" + datePath + "/" + uniqueFileName;
					ProductImage newProductImage = new ProductImage(relativePath, product);
					product.addImage(newProductImage);
				} catch (IOException e) {
					throw new ProductException(FILE_UPLOAD_FAILED);
				}
			}
		}

		productRepository.save(product);
	}

	@Override
	@Transactional
	public void updateStock(Long productId, ProductStockUpdateRequestDto requestDto) {
		// 1. 상품 조회
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

		log.info("재고 수량을 수정합니다: (ID: {}, 기존 수량: {}, 새 수량: {})",
			productId, product.getStockQuantity(), requestDto.getStockQuantity());

		// 2. 재고 수량 변경
		product.setStockQuantity(requestDto.getStockQuantity());

		// 3. 변경된 내용 저장 (트랜잭션 종료 시 자동 반영)
		productRepository.save(product);
	}

	@Override
	@Transactional
	public void deleteProduct(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

		if (orderItemRepository.existsByProduct(product)) {
			throw new ProductException(ProductErrorCode.PRODUCT_IN_USE);
		}

		productRepository.delete(product);
	}


	private void saveFile(MultipartFile multipartFile, String datePath, String fileName) throws IOException {
		String fullPathString = Paths.get(uploadDir, datePath).toString();
		File directory = new File(fullPathString);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		Path filePath = Paths.get(fullPathString, fileName);
		multipartFile.transferTo(filePath);
		if (!isImageFile(filePath)) {
			Files.delete(filePath);
			throw new ProductException(INVALID_IMAGE_FILE);
		}
	}

	private boolean isImageFile(Path filePath) throws IOException {
		String contentType = Files.probeContentType(filePath);
		return contentType != null && contentType.startsWith("image");
	}

	private void validateFiles(List<MultipartFile> multipartFiles) {
		long totalSize = 0;
		for (MultipartFile file : multipartFiles) {
			if (file.getSize() > MAX_FILE_SIZE) {
				throw new ProductException(FILE_SIZE_EXCEEDED);
			}
			totalSize += file.getSize();
		}
		if (totalSize > MAX_TOTAL_SIZE) {
			throw new ProductException(TOTAL_FILE_SIZE_EXCEEDED);
		}
	}

	private String getDatePath() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
	}

	private String generateUniqueFileName(String originalFilename) {
		String ext = "";
		if (originalFilename != null && originalFilename.contains(".")) {
			ext = originalFilename.substring(originalFilename.lastIndexOf("."));
		}
		return UUID.randomUUID().toString() + ext;
	}
}