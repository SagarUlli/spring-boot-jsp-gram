package org.jsp.jsp_gram.service;

import java.util.List;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.UserResponse;
import org.jsp.jsp_gram.exception.AuthException;
import org.jsp.jsp_gram.mapper.UserMapper;
import org.jsp.jsp_gram.model.User;
import org.jsp.jsp_gram.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

	private final UserRepository userRepository;
	private final CurrentUserService currentUserService;
	private final NotificationService notificationService;
	private final FeedCacheService feedCacheService;

	private User getUserById(int id) {

		return userRepository.findById(id).orElseThrow(() -> new AuthException("User not found"));
	}

	/**
	 * Get user suggestions.
	 */

	@Transactional(readOnly = true)
	public ApiResponse<List<UserResponse>> getSuggestions() {

		User user = currentUserService.getLoggedInUser();

		List<User> suggestions = userRepository.findByVerifiedTrue();

		suggestions.removeIf(u -> u.getId() == user.getId() || user.getFollowing().contains(u));

		List<UserResponse> response = suggestions.stream().map(UserMapper::toResponse).toList();

		return new ApiResponse<>(true, "Suggestions fetched successfully", response);
	}

	/**
	 * Follow User.
	 */

	@Transactional
	public ApiResponse<Void> followUser(int id) {

		User user = currentUserService.getLoggedInUser();

		User toFollow = getUserById(id);

		if (user.getId() == toFollow.getId()) {
			throw new AuthException("You cannot follow yourself");
		}

		if (user.getFollowing().contains(toFollow)) {
			throw new AuthException("User is already being followed");
		}

		user.getFollowing().add(toFollow);
		toFollow.getFollowers().add(user);

		userRepository.save(user);
		userRepository.save(toFollow);

		// The feed now includes the followed user's posts.
		feedCacheService.evict(user.getId());

		// Create notification for the followed user.
		notificationService.createFollowNotification(user, toFollow);

		return new ApiResponse<>(true, "User followed successfully");
	}

	/**
	 * Unfollow User.
	 */

	@Transactional
	public ApiResponse<Void> unfollowUser(int id) {

		User user = currentUserService.getLoggedInUser();

		User toUnfollow = getUserById(id);

		if (!user.getFollowing().contains(toUnfollow)) {
			throw new AuthException("User is not being followed");
		}

		user.getFollowing().remove(toUnfollow);
		toUnfollow.getFollowers().remove(user);

		userRepository.save(user);
		userRepository.save(toUnfollow);

		feedCacheService.evict(user.getId());

		return new ApiResponse<>(true, "User unfollowed successfully");
	}

	/**
	 * Get Followers.
	 */

	@Transactional(readOnly = true)
	public ApiResponse<List<UserResponse>> getFollowers() {

		User user = currentUserService.getLoggedInUser();

		List<UserResponse> response = user.getFollowers().stream().map(UserMapper::toResponse).toList();

		return new ApiResponse<>(true, "Followers fetched successfully", response);
	}

	/**
	 * Get Following.
	 */

	@Transactional(readOnly = true)
	public ApiResponse<List<UserResponse>> getFollowing() {

		User user = currentUserService.getLoggedInUser();

		List<UserResponse> response = user.getFollowing().stream().map(UserMapper::toResponse).toList();

		return new ApiResponse<>(true, "Following fetched successfully", response);
	}
}