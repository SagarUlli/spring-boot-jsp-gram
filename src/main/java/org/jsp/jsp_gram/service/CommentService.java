package org.jsp.jsp_gram.service;

import java.util.List;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.CommentRequest;
import org.jsp.jsp_gram.dto.CommentResponse;
import org.jsp.jsp_gram.exception.AuthException;
import org.jsp.jsp_gram.mapper.CommentMapper;
import org.jsp.jsp_gram.model.Comment;
import org.jsp.jsp_gram.model.Post;
import org.jsp.jsp_gram.model.User;
import org.jsp.jsp_gram.repository.CommentRepository;
import org.jsp.jsp_gram.repository.PostRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final SessionService sessionService;

	/**
	 * Returns post by id.
	 */
	private Post getPostById(int id) {

		return postRepository.findById(id).orElseThrow(() -> new AuthException("Post not found"));
	}

	/**
	 * Add Comment.
	 */
	public ApiResponse<CommentResponse> addComment(int id, CommentRequest request, HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		Post post = getPostById(id);

		Comment comment = new Comment();

		comment.setComment(request.getComment());
		comment.setUser(user);

		post.getComments().add(comment);

		postRepository.save(post);

		return new ApiResponse<>(true, "Comment added successfully", CommentMapper.toResponse(comment));
	}

	/**
	 * Get all comments of a post.
	 */
	public ApiResponse<List<CommentResponse>> getComments(int id, HttpSession session) {

		sessionService.getLoggedInUser(session);

		Post post = getPostById(id);

		List<CommentResponse> response = post.getComments().stream()
				.sorted((c1, c2) -> c2.getCommentedTime().compareTo(c1.getCommentedTime()))
				.map(CommentMapper::toResponse).toList();

		return new ApiResponse<>(true, "Comments fetched successfully", response);
	}

	/**
	 * Returns comment by id.
	 */
	private Comment getCommentById(int id) {

		return commentRepository.findById(id).orElseThrow(() -> new AuthException("Comment not found"));
	}

	/**
	 * Delete Comment.
	 */
	public ApiResponse<Void> deleteComment(int id, HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		Comment comment = getCommentById(id);

		if (comment.getUser().getId() != user.getId()) {
			throw new AuthException("Unauthorized");
		}

		commentRepository.delete(comment);

		return new ApiResponse<>(true, "Comment deleted successfully");
	}
}
