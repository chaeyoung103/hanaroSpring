package com.example.hanaro.domain.user.controller;

import com.example.hanaro.domain.user.dto.request.TokenReissueRequestDto;
import com.example.hanaro.domain.user.dto.request.UserSignInRequestDto;
import com.example.hanaro.domain.user.dto.request.UserSignUpRequestDto;
import com.example.hanaro.domain.user.dto.response.UserSignInResponseDto;
import com.example.hanaro.domain.user.service.UserService;
import com.example.hanaro.global.response.BaseErrorResponse;
import com.example.hanaro.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@Tag(name = "[일반유저] 인증", description = "일반유저 회원가입, 로그인")
	@Operation(summary = "회원가입", description = "새로운 사용자를 등록")
	@ApiResponse(responseCode = "200", description = "회원가입 성공",
		content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	@ApiResponse(responseCode = "409", description = "이메일 또는 닉네임 중복",
		content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	@PostMapping("/signup")
	public ResponseEntity<BaseResponse<Void>> signUp(@Valid @RequestBody UserSignUpRequestDto requestDto) {
		userService.signUp(requestDto);
		return ResponseEntity.ok(new BaseResponse<>());
	}

	@Tag(name = "[일반유저] 인증", description = "일반유저 회원가입, 로그인")
	@Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 토큰을 발급받습니다.")
	@ApiResponse(responseCode = "200", description = "로그인 성공",
		content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	@ApiResponse(responseCode = "404", description = "존재하지 않는 회원",
		content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	@ApiResponse(responseCode = "401", description = "비밀번호 불일치",
		content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	@PostMapping("/signin")
	public ResponseEntity<BaseResponse<UserSignInResponseDto>> signIn(@Valid @RequestBody UserSignInRequestDto requestDto) {
		UserSignInResponseDto response = userService.signIn(requestDto);
		return ResponseEntity.ok(new BaseResponse<>(response));
	}

	@Tag(name = "[공용] 인증", description = "토큰 재발급")
	@Operation(summary = "토큰 재발급", description = "Refresh Token을 사용하여 새로운 Access/Refresh Token을 발급받습니다.")
	@ApiResponse(responseCode = "200", description = "토큰 재발급 성공")
	@ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token",
		content = @Content(schema = @Schema(implementation = BaseErrorResponse.class)))
	@PostMapping("/reissue")
	public ResponseEntity<BaseResponse<UserSignInResponseDto>> reissue(
		@RequestBody TokenReissueRequestDto requestDto) {
		UserSignInResponseDto response = userService.reissueToken(requestDto.getRefreshToken());
		return ResponseEntity.ok(new BaseResponse<>(response));
	}
}