package org.jsp.jsp_gram.controller;

import org.jsp.jsp_gram.dto.Post;
import org.jsp.jsp_gram.dto.User;
import org.jsp.jsp_gram.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AppController {

	private final UserService service;

	public AppController(UserService service) {
		this.service = service;
	}

	/* ================= LOGIN / REGISTER ================= */
	@GetMapping({ "/", "/login" })
	public String loadLogin() {
		return "login.html";
	}

	@GetMapping("/register")
	public String loadRegister(ModelMap map, User user) {
		return service.loadRegister(map, user);
	}

	@PostMapping("/register")
	public String register(@Valid User user, BindingResult result, HttpSession session) {
		return service.register(user, result, session);
	}

	@GetMapping("/otp/{id}")
	public String loadOtpPage(@PathVariable int id, ModelMap map) {
		map.put("id", id);
		return "user-otp.html";
	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam int id, @RequestParam int otp, HttpSession session) {
		return service.verifyOtp(id, otp, session);
	}

	@GetMapping("/resend-otp/{id}")
	public String resendOtp(@PathVariable int id, HttpSession session) {
		return service.resendOtp(id, session);
	}

	@PostMapping("/login")
	public String login(@RequestParam String username, String password, HttpSession session) {
		return service.login(username, password, session);
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		return service.logout(session);
	}

	/* ================= HOME ================= */
	@GetMapping("/home")
	public String loadHome(HttpSession session, ModelMap map) {
		return service.loadHome(session, map);
	}

	/* ================= PROFILE ================= */
	@GetMapping("/profile")
	public String loadProfile(HttpSession session, ModelMap map) {
		return service.profile(session, map);
	}

	@GetMapping("/edit-profile")
	public String editProfile(HttpSession session) {
		return service.editProfile(session);
	}

	@PostMapping("/update-profile")
	public String updateProfile(@RequestParam MultipartFile image, @RequestParam String bio, HttpSession session) {
		return service.updateProfile(image, session, bio);
	}

	/* ================= POSTS ================= */
	@GetMapping("/addpost")
	public String loadAddPost(HttpSession session) {
		return service.loadAddPost(session);
	}

	@PostMapping("/addpost")
	public String addPost(@ModelAttribute Post post, HttpSession session) {
		return service.addPost(post, session);
	}

	@GetMapping("/delete/{id}")
	public String deletePost(@PathVariable int id, HttpSession session) {
		return service.deletePost(id, session);
	}

	@GetMapping("/edit/{id}")
	public String editPost(HttpSession session, @PathVariable int id, ModelMap map) {
		return service.editPost(session, id, map);
	}

	@PostMapping("/update-post")
	public String updatePost(@ModelAttribute Post post, HttpSession session) throws Exception {
		return service.updatePost(post, session);
	}

	/* ================= FOLLOW / SUGGESTIONS ================= */
	@GetMapping("/suggestions")
	public String suggestions(ModelMap map, HttpSession session) {
		return service.viewSuggestions(map, session);
	}

	@GetMapping("/follow/{id}")
	public String follow(@PathVariable int id, HttpSession session) {
		return service.followUser(id, session);
	}

	@GetMapping("/unfollow/{id}")
	public String unfollow(HttpSession session, @PathVariable int id) {
		return service.unfollow(session, id);
	}

	@GetMapping("/followers")
	public String getFollowers(HttpSession session, ModelMap map) {
		return service.getFollowers(session, map);
	}

	@GetMapping("/following")
	public String getFollowing(HttpSession session, ModelMap map) {
		return service.getFollowing(session, map);
	}

	@GetMapping("/view-profile/{id}")
	public String viewProfile(@PathVariable int id, HttpSession session, ModelMap map) {
		return service.viewProfile(id, session, map);
	}

	/* ================= LIKE / COMMENT ================= */
	@GetMapping("/like/{id}")
	public String likePost(@PathVariable int id, HttpSession session) {
		return service.likePost(id, session);
	}

	@GetMapping("/dislike/{id}")
	public String dislikePost(@PathVariable int id, HttpSession session) {
		return service.dislikePost(id, session);
	}

	@GetMapping("/comment/{id}")
	public String loadCommentPage(HttpSession session, @PathVariable int id, ModelMap map) {
		return service.loadCommentPage(session, id, map);
	}

	@PostMapping("/comment/{id}")
	public String comment(HttpSession session, @PathVariable int id, @RequestParam String comment) {
		return service.comment(session, id, comment);
	}

	/* ================= PRIME ================= */
	@GetMapping("/prime")
	public String prime(HttpSession session, ModelMap map) throws RazorpayException {
		return service.prime(session, map);
	}

	@PostMapping("/prime")
	public String prime(HttpSession session) throws RazorpayException {
		return service.prime(session);
	}
}
