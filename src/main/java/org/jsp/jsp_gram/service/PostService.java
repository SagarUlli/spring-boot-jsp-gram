package org.jsp.jsp_gram.service;

import java.util.ArrayList;
import java.util.List;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.PostRequest;
import org.jsp.jsp_gram.dto.PostResponse;
import org.jsp.jsp_gram.exception.AuthException;
import org.jsp.jsp_gram.helper.CloudinaryHelper;
import org.jsp.jsp_gram.mapper.PostMapper;
import org.jsp.jsp_gram.model.Post;
import org.jsp.jsp_gram.model.User;
import org.jsp.jsp_gram.repository.PostRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

	private final CloudinaryHelper cloudinaryHelper;

	private final PostRepository postRepository;

	private final SessionService sessionService;

	private Post getPostById(int id) {

		return postRepository.findById(id).orElseThrow(() -> new AuthException("Post not found"));

	}

	private void validatePostOwner(Post post, User user) {

		if (post.getUser().getId() != user.getId()) {

			throw new AuthException("Unauthorized");

		}

	}

	public ApiResponse<PostResponse> createPostRest(PostRequest request, HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		Post post = new Post();

		post.setCaption(request.getCaption());

		post.setUser(user);

		if (request.getImage() != null && !request.getImage().isEmpty()) {

			post.setImageUrl(cloudinaryHelper.saveImage(request.getImage()));

		}

		post = postRepository.save(post);

		return new ApiResponse<>(true, "Post created successfully", PostMapper.toResponse(post, user));

	}

	public ApiResponse<List<PostResponse>> getFeed(HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		List<User> users = new ArrayList<>(user.getFollowing());

		users.add(user);

		List<Post> posts = postRepository.findByUserInOrderByPostedTimeDesc(users);

		List<PostResponse> response = posts.stream().map(post -> PostMapper.toResponse(post, user)).toList();

		return new ApiResponse<>(true, "Posts fetched successfully", response);

	}

	public ApiResponse<PostResponse> getPost(int id, HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		Post post = getPostById(id);

		return new ApiResponse<>(true, "Post fetched successfully", PostMapper.toResponse(post, user));

	}

	public ApiResponse<PostResponse> updatePost(int id, PostRequest request, HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		Post post = getPostById(id);

		validatePostOwner(post, user);

		post.setCaption(request.getCaption());

		if (request.getImage() != null && !request.getImage().isEmpty()) {

			post.setImageUrl(cloudinaryHelper.saveImage(request.getImage()));

		}

		postRepository.save(post);

		return new ApiResponse<>(true, "Post updated successfully", PostMapper.toResponse(post, user));

	}

	public ApiResponse<Void> deletePost(int id, HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		Post post = getPostById(id);

		validatePostOwner(post, user);

		postRepository.delete(post);

		return new ApiResponse<>(true, "Post deleted successfully");

	}

	// LIKE

	public ApiResponse<PostResponse> likePost(int id, HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		Post post = getPostById(id);

		if (!post.hasLiked(user.getId())) {

			post.getLikedUsers().add(user);

			postRepository.save(post);

		}

		return new ApiResponse<>(true, "Post liked", PostMapper.toResponse(post, user));

	}

	// UNLIKE

	public ApiResponse<PostResponse> unlikePost(int id, HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		Post post = getPostById(id);

		post.getLikedUsers().removeIf(u -> u.getId() == user.getId());

		postRepository.save(post);

		return new ApiResponse<>(true, "Post unliked", PostMapper.toResponse(post, user));

	}

}