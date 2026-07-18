package org.jsp.jsp_gram.service;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.ProfileRequest;
import org.jsp.jsp_gram.dto.UserResponse;
import org.jsp.jsp_gram.exception.AuthException;
import org.jsp.jsp_gram.helper.CloudinaryHelper;
import org.jsp.jsp_gram.mapper.UserMapper;
import org.jsp.jsp_gram.model.User;
import org.jsp.jsp_gram.repository.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

	private final UserRepository userRepository;
	private final SessionService sessionService;
	private final CloudinaryHelper cloudinaryHelper;

	/**
	 * Get logged-in user's profile.
	 */
	public ApiResponse<UserResponse> getMyProfile(HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		return new ApiResponse<>(true, "Profile fetched successfully", UserMapper.toResponse(user));
	}

	/**
	 * Get another user's profile.
	 */
	public ApiResponse<UserResponse> getProfile(int id, HttpSession session) {

		sessionService.getLoggedInUser(session);

		User user = userRepository.findById(id).orElseThrow(() -> new AuthException("User not found"));

		return new ApiResponse<>(true, "Profile fetched successfully", UserMapper.toResponse(user));
	}

	/**
	 * Update profile.
	 */
	public ApiResponse<UserResponse> updateProfile(ProfileRequest request, HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		user.setFirstname(request.getFirstname());
		user.setLastname(request.getLastname());
		user.setBio(request.getBio());

		if (request.getImage() != null && !request.getImage().isEmpty()) {
			user.setImageUrl(cloudinaryHelper.saveImage(request.getImage()));
		}

		user = userRepository.save(user);

		session.setAttribute("user", user);

		return new ApiResponse<>(true, "Profile updated successfully", UserMapper.toResponse(user));
	}
}