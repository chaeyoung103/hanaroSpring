package com.example.hanaro.domain.product.service;

import com.example.hanaro.domain.product.dto.request.ProductCreateRequestDto;
import com.example.hanaro.domain.product.dto.response.ProductDto;
import com.example.hanaro.domain.product.entity.Product;
import com.example.hanaro.domain.product.entity.ProductImage;
import com.example.hanaro.domain.product.exception.ProductException;
import com.example.hanaro.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
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

	@Value("${file.upload-dir}")
	private String uploadDir;

	private final long MAX_FILE_SIZE = 512 * 1024; // 512KB
	private final long MAX_TOTAL_SIZE = 3 * 1024 * 1024; // 3MB

	@Override
	@Transactional
	public void createProduct(ProductCreateRequestDto requestDto) {
		// 1. 상품명 중복 검사
		if (productRepository.findByName(requestDto.getName()).isPresent()) {
			throw new ProductException(DUPLICATE_PRODUCT_NAME);
		}

		// 2. Product 객체를 메모리에 생성 (아직 DB 저장 X)
		Product product = Product.builder()
			.name(requestDto.getName())
			.price(requestDto.getPrice())
			.description(requestDto.getDescription())
			.stockQuantity(requestDto.getStockQuantity())
			.build();

		List<MultipartFile> images = requestDto.getImages();

		// 3. 이미지가 있다면 파일 처리 및 관계 설정 로직을 이 곳에서 직접 수행
		if (images != null && !images.isEmpty()) {
			validateFiles(images); // 파일 유효성 검사
			String datePath = getDatePath();

			for (MultipartFile multipartFile : images) {
				if (multipartFile.isEmpty()) continue;

				try {
					String uniqueFileName = generateUniqueFileName(multipartFile.getOriginalFilename());
					// 파일을 실제 경로에 저장
					saveFile(multipartFile, datePath, uniqueFileName);

					String relativePath = "/upload/" + datePath + "/" + uniqueFileName;

					// ProductImage 객체 생성
					ProductImage productImage = new ProductImage();
					productImage.setImageUrl(relativePath);

					// Product의 헬퍼 메서드를 사용해 양방향 관계를 설정 (가장 중요!)
					product.addImage(productImage);

				} catch (IOException e) {
					log.error("Image upload failed", e);
					throw new ProductException(FILE_UPLOAD_FAILED);
				}
			}
		}

		// 4. 모든 관계 설정이 끝난 후, Product를 한 번만 저장
		// CascadeType.ALL 설정 덕분에 ProductImage도 함께 저장됨
		productRepository.save(product);
		log.info("새로운 상품이 등록되었습니다: {}", product.getName());
	}

	// [✨수정✨] 관리자용: 모든 상품을 조회합니다.
	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> findAllProducts() {
		log.info("관리자가 전체 상품 목록을 조회합니다.");
		return productRepository.findAll().stream()
			.map(ProductDto::fromEntity)
			.collect(Collectors.toList());
	}

	// [✨새로운 코드✨] 사용자용: 키워드로 상품을 검색합니다.
	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> searchProducts(String keyword) {
		log.info("사용자가 상품을 검색합니다. 키워드: {}", keyword);
		// 키워드가 없거나 비어있으면 모든 상품을 반환하고, 있으면 검색 결과를 반환합니다.
		List<Product> products = (keyword == null || keyword.isBlank())
			? productRepository.findAll()
			: productRepository.findByNameContainingIgnoreCase(keyword);

		return products.stream()
			.map(ProductDto::fromEntity)
			.collect(Collectors.toList());
	}


	// ---👇 Private Helper Methods ---

	private void saveFile(MultipartFile multipartFile, String datePath, String fileName) throws IOException {
		String fullPathString = Paths.get(uploadDir, datePath).toString();
		File directory = new File(fullPathString);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		Path filePath = Paths.get(fullPathString, fileName);
		multipartFile.transferTo(filePath);

		// 저장 후 이미지 파일이 맞는지 검사
		if (!isImageFile(filePath)) {
			// 이미지 파일이 아니면 저장했던 파일을 삭제하고 예외를 던지는 것이 좋음
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