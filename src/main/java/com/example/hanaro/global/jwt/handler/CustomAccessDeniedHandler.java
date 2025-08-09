package com.example.hanaro.global.jwt.handler;

import com.example.hanaro.global.response.BaseErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.example.hanaro.global.response.BaseErrorCode.ACCESS_DENIED;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
		throws IOException, ServletException {
		log.warn("Access Denied: {}", accessDeniedException.getMessage());

		// 1. BaseErrorResponse 생성
		BaseErrorResponse errorResponse = new BaseErrorResponse(ACCESS_DENIED);

		// 2. HTTP 응답 설정
		response.setStatus(ACCESS_DENIED.getStatus());
		response.setContentType("application/json;charset=UTF-8");

		// 3. JSON으로 변환하여 응답 본문에 쓰기
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}