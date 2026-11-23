package org.jsp.jsp_gram.service;

import java.util.List;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.PostResponse;
import org.jsp.jsp_gram.exception.AuthException;
import org.jsp.jsp_gram.mapper.PostMapper;
import org.jsp.jsp_gram.model.Post;
import org.jsp.jsp_gram.model.User;
import org.jsp.jsp_gram.repository.PostRepository;
import org.jsp.jsp_gram.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {

	private final UserRepository userRepository;
	private final CurrentUserService currentUserService;
	private final FeedCacheService feedCacheService;
	private final PostRepository postRepository;

	/**
	 * Bookmark a post.
	 */

	@Transactional
	public ApiResponse<PostResponse> bookmarkPost(int postId) {

		User user = currentUserService.getLoggedInUser();

		Post post = postRepository.findById(postId).orElseThrow(() -> new AuthException("Post not found"));

		boolean alreadyBookmarked = user.getBookmarkedPosts().stream()
				.anyMatch(bookmarkedPost -> bookmarkedPost.getId() == post.getId());

		if (!alreadyBookmarked) {
			user.getBookmarkedPosts().add(post);
			userRepository.save(user);
			feedCacheService.evict(user.getId());
		}

		return new ApiResponse<>(true, alreadyBookmarked ? "Post already bookmarked" : "Post bookmarked successfully",
				PostMapper.toResponse(post, user));
	}

	/**
	 * Remove a post from bookmarks.
	 */

	@Transactional
	public ApiResponse<PostResponse> unbookmarkPost(int postId) {

		User user = currentUserService.getLoggedInUser();

		Post post = postRepository.findById(postId).orElseThrow(() -> new AuthException("Post not found"));

		boolean removed = user.getBookmarkedPosts().removeIf(bookmarkedPost -> bookmarkedPost.getId() == post.getId());

		if (removed) {
			userRepository.save(user);
			feedCacheService.evict(user.getId());
		}

		return new ApiResponse<>(true, removed ? "Post removed from bookmarks" : "Post was not bookmarked",
				PostMapper.toResponse(post, user));
	}

	/**
	 * Get all posts bookmarked by the logged-in user.
	 */

	@Transactional(readOnly = true)
	public ApiResponse<List<PostResponse>> getBookmarks() {

		User user = currentUserService.getLoggedInUser();

		List<PostResponse> response = user.getBookmarkedPosts().stream().map(post -> PostMapper.toResponse(post, user))
				.toList();

		return new ApiResponse<>(true, "Bookmarks fetched successfully", response);
	}
}