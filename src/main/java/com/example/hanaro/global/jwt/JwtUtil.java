package com.example.hanaro.global.jwt;

import com.example.hanaro.global.jwt.exception.CustomJwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

	private final SecretKey secretKey;
	private final long accessTokenExpMs;
	private final long refreshTokenExpMs;

	public JwtUtil(
		@Value("${jwt.secret.key}") String secret,
		@Value("${jwt.token.access-expired-time-ms}") long accessTokenExpMs,
		@Value("${jwt.token.refresh-expired-time-ms}") long refreshTokenExpMs
	) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessTokenExpMs = accessTokenExpMs;
		this.refreshTokenExpMs = refreshTokenExpMs;
	}

	// Access Token 생성
	public String createAccessToken(String email, String role) {
		return Jwts.builder()
			.claim("email", email)
			.claim("role", role)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpMs))
			.signWith(secretKey)
			.compact();
	}

	//Refresh Token 생성
	public String createRefreshToken() {
		return Jwts.builder()
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpMs))
			.signWith(secretKey)
			.compact();
	}


	// 토큰 검증 및 Claims 추출
	public Claims validateToken(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (MalformedJwtException e) {
			throw new CustomJwtException("Malformed Token");
		} catch (ExpiredJwtException e) {
			throw new CustomJwtException("Expired Token");
		} catch (JwtException e) {
			throw new CustomJwtException("Invalid Token");
		}
	}
}