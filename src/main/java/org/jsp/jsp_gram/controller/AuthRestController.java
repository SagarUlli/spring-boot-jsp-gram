package org.jsp.jsp_gram.controller;

import java.util.List;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.AuthResponse;
import org.jsp.jsp_gram.dto.LoginRequest;
import org.jsp.jsp_gram.dto.OtpRequest;
import org.jsp.jsp_gram.dto.RegisterRequest;
import org.jsp.jsp_gram.dto.UserResponse;
import org.jsp.jsp_gram.mapper.UserMapper;
import org.jsp.jsp_gram.model.User;
import org.jsp.jsp_gram.service.CurrentUserService;
import org.jsp.jsp_gram.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthRestController {

	private final UserService service;
	private final CurrentUserService currentUserService;

	@PostMapping("/login")
	public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request) {

		return service.loginRest(request.getUsername(), request.getPassword());
	}

	@PostMapping("/register")
	public ApiResponse<Integer> register(@RequestBody RegisterRequest request) {

		return service.registerRest(request);
	}

	@PostMapping("/verify-otp")
	public ApiResponse<Void> verifyOtp(@RequestBody OtpRequest request) {

		return service.verifyOtpRest(request.getUserId(), request.getOtp());
	}

	@PostMapping("/resend-otp")
	public ApiResponse<Void> resendOtp(@RequestBody OtpRequest request) {
		return service.resendOtpRest(request.getUserId());
	}

	@GetMapping("/me")
	public ApiResponse<UserResponse> getLoggedInUser() {

		User user = currentUserService.getLoggedInUser();

		return new ApiResponse<>(true, "User Found", UserMapper.toResponse(user));
	}

	@PostMapping("/logout")
	public ApiResponse<Void> logout() {

		return service.logoutRest();

	}

	@GetMapping("/search")
	public ApiResponse<List<UserResponse>> searchUsers(@RequestParam String username) {

		return service.searchUsers(username);
	}
}