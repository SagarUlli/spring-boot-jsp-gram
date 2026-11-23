package org.jsp.jsp_gram.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Reads "Authorization: Bearer <jwt>", and if the token is valid puts the user
 * id into the SecurityContext. An invalid or missing token simply leaves the
 * request unauthenticated; the entry point then answers 401.
 *
 * Deliberately NOT a @Component: Spring Boot would register it a second time
 * as a plain servlet filter. SecurityConfig creates it instead.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String BEARER = "Bearer ";

	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String header = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (header != null && header.startsWith(BEARER)) {
			try {
				int userId = jwtService.parseUserId(header.substring(BEARER.length()).trim());

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId,
						null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

				SecurityContextHolder.getContext().setAuthentication(authentication);

			} catch (JwtException | IllegalArgumentException e) {
				SecurityContextHolder.clearContext();
			}
		}

		chain.doFilter(request, response);
	}
}
