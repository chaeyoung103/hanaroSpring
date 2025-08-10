package com.example.hanaro.domain.user.controller;

import com.example.hanaro.domain.user.dto.request.AdminSignInRequestDto;
import com.example.hanaro.domain.user.dto.response.UserDto;
import com.example.hanaro.domain.user.dto.response.UserSignInResponseDto;
import com.example.hanaro.domain.user.service.AdminService;
import com.example.hanaro.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Authentication")
public class AdminController {

	private final AdminService adminService;

	@Tag(name = "[관리자] 인증", description = "관리자 전용 로그인")
	@Operation(summary = "관리자 로그인", description = "관리자 계정으로 로그인합니다.")
	@PostMapping("/signin")
	public ResponseEntity<BaseResponse<UserSignInResponseDto>> signIn(@Valid @RequestBody AdminSignInRequestDto requestDto) {
		UserSignInResponseDto response = adminService.signIn(requestDto);
		return ResponseEntity.ok(new BaseResponse<>(response));
	}

	@Tag(name = "[관리자] 회원관리", description = "회원 관리 기능")
	@Operation(summary = "전체 회원 목록 조회", description = "모든 사용자의 정보를 조회합니다. (ADMIN 권한 필요)")
	@GetMapping("/users")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BaseResponse<List<UserDto>>> getUsers() {
		List<UserDto> users = adminService.findUsers();
		return ResponseEntity.ok(new BaseResponse<>(users));
	}

	@Tag(name = "[관리자] 회원관리", description = "회원 관리 기능")
	@Operation(summary = "특정 회원 삭제", description = "사용자 ID로 회원을 삭제합니다. (ADMIN 권한 필요)")
	@DeleteMapping("/users/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable Long userId) {
		adminService.deleteUser(userId);
		return ResponseEntity.ok(new BaseResponse<>());
	}
}