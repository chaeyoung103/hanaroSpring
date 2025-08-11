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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

	/**
	 * 새로운 상품을 등록합니다.
	 * @param requestDto 상품 정보 및 이미지 파일
	 */
	@Override
	@Transactional
	public void createProduct(ProductCreateRequestDto requestDto) {
		log.info("======== 상품 등록 시작: {} ========", requestDto.getName());
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
			validateFiles(images);
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
		log.info("======== 상품 등록 성공: {} ========", product.getName());
	}

	// 관리자 - 상품목록 조회
	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> findAllProducts() {
		log.info("관리자가 전체 상품 목록을 조회합니다.");
		return productRepository.findAll().stream()
			.map(ProductDto::fromEntity)
			.collect(Collectors.toList());
	}

	// 사용자 - 상품 검색
	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> searchProducts(String keyword) {
		log.info("사용자 상품 검색. 키워드: '{}'", keyword);
		List<Product> products = (keyword == null || keyword.isBlank())
			? productRepository.findAll()
			: productRepository.findByNameContainingIgnoreCase(keyword);

		return products.stream()
			.map(ProductDto::fromEntity)
			.collect(Collectors.toList());
	}

	// 사용자 - 상품 상세 조회
	@Override
	@Transactional(readOnly = true)
	public ProductDto findProductById(Long productId) {
		log.info("상품 상세 정보 조회 (ID: {})", productId);
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

		return ProductDto.fromEntity(product);
	}

	// 관리자 - 상품 수정
	@Override
	@Transactional
	public void updateProduct(Long productId, ProductCreateRequestDto requestDto) {
		log.info("======== 상품 수정 시작: (ID: {}) ========", productId);
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

		product.setName(requestDto.getName());
		product.setPrice(requestDto.getPrice());
		product.setDescription(requestDto.getDescription());
		product.setStockQuantity(requestDto.getStockQuantity());

		// 새로운 이미지가 제공된 경우에만 처리
		List<MultipartFile> newImages = requestDto.getImages();
		if (newImages != null && !newImages.isEmpty()) {
			log.info(" >> 새로운 이미지 감지, 기존 이미지 삭제 및 신규 이미지로 교체 작업을 시작합니다.");
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
					log.error("상품 수정 중 이미지 업로드 실패", e);
					throw new ProductException(FILE_UPLOAD_FAILED);
				}
			}
		}
		productRepository.save(product);
		log.info("======== 상품 수정 성공: {} (ID: {}) ========", product.getName(), productId);
	}

	// 관리자 - 재고 수정
	@Override
	@Transactional
	public void updateStock(Long productId, ProductStockUpdateRequestDto requestDto) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

		log.info("======== 재고 수정: (상품명: {}, 기존: {}, 변경: {}) ========",
			product.getName(), product.getStockQuantity(), requestDto.getStockQuantity());

		product.setStockQuantity(requestDto.getStockQuantity());
	}

	// 관리자 - 상품 삭제
	@Override
	@Transactional
	public void deleteProduct(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

		log.warn("======== 상품 삭제 시도: {} (ID: {}) ========", product.getName(), productId);

		if (orderItemRepository.existsByProduct(product)) {
			log.error(" >> 삭제 실패: 상품 '{}'에 대한 주문 내역이 존재합니다.", product.getName());
			throw new ProductException(ProductErrorCode.PRODUCT_IN_USE);
		}

		productRepository.delete(product);
		log.warn("======== 상품 삭제 완료: {} (ID: {}) ========", product.getName(), productId);
	}

	// 헬퍼 메소드들
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