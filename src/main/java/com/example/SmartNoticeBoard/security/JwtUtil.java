package com.example.SmartNoticeBoard.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	// Must be 32+ chars for HS256
	private static final String SECRET = "0123456789abcdef0123456789abcdef";
	private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

	public static Key getKey() {
		return key;
	}

	// Generate access token
	public static String generateToken(String username) {
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 min
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	// Generate refresh token
	public static String generateRefreshToken(String username) {
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 3 * 60 * 1000)) // 3 min
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	// Extract username
	public static String extractUsername(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException e) {
			return false;
		}
	}
}
