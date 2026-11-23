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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PostRestController {

	private final PostService postService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<PostResponse> createPost(@ModelAttribute PostRequest request) {

		return postService.createPostRest(request);

	}

	@GetMapping
	public ApiResponse<List<PostResponse>> getFeed() {

		return postService.getFeed();

	}

	@GetMapping("/{id:\\d+}")
	public ApiResponse<PostResponse> getPost(@PathVariable int id) {

		return postService.getPost(id);

	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<PostResponse> updatePost(@PathVariable int id, @ModelAttribute PostRequest request) {

		return postService.updatePost(id, request);

	}

	@DeleteMapping("/{id}")
	public ApiResponse<Void> deletePost(@PathVariable int id) {

		return postService.deletePost(id);

	}

	@PostMapping("/{id}/like")
	public ApiResponse<PostResponse> likePost(@PathVariable int id) {

		return postService.likePost(id);

	}

	@DeleteMapping("/{id}/like")
	public ApiResponse<PostResponse> unlikePost(@PathVariable int id) {

		return postService.unlikePost(id);

	}

	@PostMapping("/{id}/share")
	public ApiResponse<PostResponse> sharePost(@PathVariable int id) {
		return postService.sharePost(id);
	}

}