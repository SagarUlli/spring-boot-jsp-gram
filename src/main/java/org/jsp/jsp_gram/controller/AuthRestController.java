package org.jsp.jsp_gram.controller;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.LoginRequest;
import org.jsp.jsp_gram.dto.OtpRequest;
import org.jsp.jsp_gram.dto.RegisterRequest;
import org.jsp.jsp_gram.dto.UserResponse;
import org.jsp.jsp_gram.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthRestController {

	private final UserService service;

	@PostMapping("/login")
	public ApiResponse<UserResponse> login(@RequestBody LoginRequest request, HttpSession session) {

		return service.loginRest(request.getUsername(), request.getPassword(), session);
	}

	@PostMapping("/register")
	public ApiResponse<Integer> register(@RequestBody RegisterRequest request, HttpSession session) {

		return service.registerRest(request, session);
	}

	@PostMapping("/verify-otp")
	public ApiResponse<Void> verifyOtp(@RequestBody OtpRequest request, HttpSession session) {

		System.out.println("Request = " + request);
		System.out.println("UserId = " + request.getUserId());
		System.out.println("OTP = " + request.getOtp());

		return service.verifyOtpRest(request.getUserId(), request.getOtp(), session);
	}

	@PostMapping("/resend-otp")
	public ApiResponse<Void> resendOtp(@RequestBody OtpRequest request, HttpSession session) {
		return service.resendOtpRest(request.getUserId(), session);
	}

	@GetMapping("/me")
	public ApiResponse<UserResponse> me(HttpSession session) {

		return service.getLoggedInUser(session);

	}

	@PostMapping("/logout")
	public ApiResponse<Void> logout(HttpSession session) {

		return service.logoutRest(session);

	}
}