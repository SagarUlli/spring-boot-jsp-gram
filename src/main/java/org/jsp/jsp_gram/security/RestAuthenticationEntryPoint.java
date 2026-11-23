package org.jsp.jsp_gram.security;

import java.io.IOException;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Returns the app's usual ApiResponse JSON (with a real 401/403 status) when
 * security rejects a request, instead of Spring's default HTML/empty body.
 */
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint, AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
			throws IOException {
		write(response, HttpServletResponse.SC_UNAUTHORIZED, "Not Logged In");
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
			throws IOException {
		write(response, HttpServletResponse.SC_FORBIDDEN, "Access denied");
	}

	private void write(HttpServletResponse response, int status, String message) throws IOException {
		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getWriter(), new ApiResponse<Void>(false, message));
	}
}
