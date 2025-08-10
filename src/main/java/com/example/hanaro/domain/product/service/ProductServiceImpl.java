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
import java.util.ArrayList;
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
		if (productRepository.findByName(requestDto.getName()).isPresent()) {
			throw new ProductException(DUPLICATE_PRODUCT_NAME);
		}

		Product product = Product.builder()
			.name(requestDto.getName())
			.price(requestDto.getPrice())
			.description(requestDto.getDescription())
			.stockQuantity(requestDto.getStockQuantity())
			.build();

		productRepository.save(product); // DB에 먼저 저장하여 ID 생성

		List<MultipartFile> images = requestDto.getImages();
		if (images != null && !images.isEmpty()) {
			try {
				// [수정] FileUploadService 호출 -> 내부 private 메서드 호출
				List<ProductImage> uploadedImages = uploadImages(product, images);
				uploadedImages.forEach(product::addImage);
			} catch (IOException e) {
				log.error("Image upload failed", e);
				throw new ProductException(FILE_UPLOAD_FAILED);
			}
		}

		productRepository.save(product);
		log.info("새로운 상품이 등록되었습니다: {}", product.getName());
	}

	// ... findProducts 메서드는 그대로 ...
	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> findProducts(Authentication authentication) {
		log.info("전체 상품 목록을 조회합니다.");
		return productRepository.findAll().stream()
			.map(ProductDto::fromEntity)
			.collect(Collectors.toList());
	}


	// ---👇 [2. FileUploadService의 메서드들을 private으로 가져옴] ---

	private List<ProductImage> uploadImages(Product product, List<MultipartFile> multipartFiles) throws IOException {
		validateFiles(multipartFiles);
		List<ProductImage> images = new ArrayList<>();
		String datePath = getDatePath();

		for (MultipartFile multipartFile : multipartFiles) {
			if (multipartFile.isEmpty()) continue;
			String uniqueFileName = generateUniqueFileName(multipartFile.getOriginalFilename());
			Path savedPath = saveFile(multipartFile, datePath, uniqueFileName);

			if (!isImageFile(savedPath)) {
				throw new ProductException(INVALID_IMAGE_FILE);
			}

			String relativePath = "/upload/" + datePath + "/" + uniqueFileName;
			ProductImage productImage = new ProductImage(relativePath, product);
			images.add(productImage);
		}
		return images;
	}

	private Path saveFile(MultipartFile multipartFile, String datePath, String fileName) throws IOException {
		String fullPathString = Paths.get(uploadDir, datePath).toString();
		File directory = new File(fullPathString);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		Path filePath = Paths.get(fullPathString, fileName);
		multipartFile.transferTo(filePath);
		return filePath;
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
		String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
		return UUID.randomUUID().toString() + ext;
	}
}