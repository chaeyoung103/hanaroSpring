package com.example.hanaro.global.util;

import com.example.hanaro.domain.product.entity.Product;
import com.example.hanaro.domain.product.entity.ProductImage;
import com.example.hanaro.domain.product.exception.ProductException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

import static com.example.hanaro.domain.product.exception.ProductErrorCode.*;

@Service
public class FileUploadService {

	@Value("${file.upload-dir}")
	private String uploadDir;

	private final long MAX_FILE_SIZE = 512 * 1024; // 512KB
	private final long MAX_TOTAL_SIZE = 3 * 1024 * 1024; // 3MB

	public List<ProductImage> uploadImages(Product product, List<MultipartFile> multipartFiles) throws IOException {

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

	// 파일 저장 로직
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

	// 이미지 파일 여부 확인
	private boolean isImageFile(Path filePath) throws IOException {
		String contentType = Files.probeContentType(filePath);
		return contentType != null && contentType.startsWith("image");
	}

	// 파일 유효성 검사 (크기 제한)
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

	// 날짜별 경로 생성
	private String getDatePath() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
	}

	// UUID 사용
	private String generateUniqueFileName(String originalFilename) {
		String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
		return UUID.randomUUID().toString() + ext;
	}
}