package org.jsp.jsp_gram.controller;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.ProfileRequest;
import org.jsp.jsp_gram.dto.UserResponse;
import org.jsp.jsp_gram.service.ProfileService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileRestController {

	private final ProfileService profileService;

	@GetMapping
	public ApiResponse<UserResponse> getMyProfile(HttpSession session) {

		return profileService.getMyProfile(session);
	}

	@GetMapping("/{id}")
	public ApiResponse<UserResponse> getProfile(@PathVariable int id, HttpSession session) {

		return profileService.getProfile(id, session);
	}

	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<UserResponse> updateProfile(@ModelAttribute ProfileRequest request, HttpSession session) {

		return profileService.updateProfile(request, session);
	}
}