package org.jsp.jsp_gram.controller;

import java.util.List;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.PostRequest;
import org.jsp.jsp_gram.dto.PostResponse;
import org.jsp.jsp_gram.service.PostService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PostRestController {

	private final PostService postService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<PostResponse> createPost(@ModelAttribute PostRequest request, HttpSession session) {

		return postService.createPostRest(request, session);

	}

	@GetMapping
	public ApiResponse<List<PostResponse>> getFeed(HttpSession session) {

		return postService.getFeed(session);

	}

	@GetMapping("/{id:\\d+}")
	public ApiResponse<PostResponse> getPost(@PathVariable int id, HttpSession session) {

		return postService.getPost(id, session);

	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<PostResponse> updatePost(@PathVariable int id, @ModelAttribute PostRequest request,
			HttpSession session) {

		return postService.updatePost(id, request, session);

	}

	@DeleteMapping("/{id}")
	public ApiResponse<Void> deletePost(@PathVariable int id, HttpSession session) {

		return postService.deletePost(id, session);

	}

	@PostMapping("/{id}/like")
	public ApiResponse<PostResponse> likePost(@PathVariable int id, HttpSession session) {

		return postService.likePost(id, session);

	}

	@DeleteMapping("/{id}/like")
	public ApiResponse<PostResponse> unlikePost(@PathVariable int id, HttpSession session) {

		return postService.unlikePost(id, session);

	}

}