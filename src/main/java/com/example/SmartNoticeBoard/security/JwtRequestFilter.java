package com.example.SmartNoticeBoard.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");

		String username = null;
		String jwt = null;

		// Token should start with Bearer
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			jwt = authHeader.substring(7);
			try {
				Claims claims = Jwts.parserBuilder().setSigningKey(JwtUtil.getKey()) // use the same secure key
						.build().parseClaimsJws(jwt).getBody();
				username = claims.getSubject();
				System.out.println("Username is " + username);
			} catch (ExpiredJwtException ex) {
                // 👇 Return 401 when token is expired
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token expired");
                return; // 🚨 Stop further filter execution
            } catch (Exception e) {
				System.out.println("Invalid JWT: " + e.getMessage());
			}
		}

		// You can attach username to request attributes or Spring Security Context
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			// You could load user roles from DB; here we just set a basic user
			UserDetails userDetails = User.withUsername(username).password("") // no need, JWT already validated
					.authorities("USER").build();

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
					userDetails.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(authToken);
		}

		chain.doFilter(request, response);
	}
}
