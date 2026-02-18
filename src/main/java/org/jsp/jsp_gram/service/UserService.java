package org.jsp.jsp_gram.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.json.JSONObject;
import org.jsp.jsp_gram.dto.Comment;
import org.jsp.jsp_gram.dto.Post;
import org.jsp.jsp_gram.dto.User;
import org.jsp.jsp_gram.helper.AES;
import org.jsp.jsp_gram.helper.CloudinaryHelper;
import org.jsp.jsp_gram.helper.EmailSender;
import org.jsp.jsp_gram.repository.PostRepository;
import org.jsp.jsp_gram.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	private static final String REDIRECT = "redirect:/";
	private static final String AMOUNT = "amount";
	private static final String CURRENCY = "currency";
	private static final String LOGIN = "login";
	private static final String PROFILE = "profile";
	private static final String POSTS = "posts";

	@Value("${razorpay.key}")
	private String razorpayKey;

	@Value("${razorpay.secret}")
	private String razorpaySecret;

	private static final Random RANDOM = new Random();

	private final UserRepository userRepository;
	private final EmailSender emailSender;
	private final CloudinaryHelper cloudinaryHelper;
	private final PostRepository postRepository;

	public UserService(UserRepository userRepository, EmailSender emailSender, CloudinaryHelper cloudinaryHelper,
			PostRepository postRepository) {
		this.userRepository = userRepository;
		this.emailSender = emailSender;
		this.cloudinaryHelper = cloudinaryHelper;
		this.postRepository = postRepository;
	}

	/* ================= SESSION HANDLING ================= */
	private String handleInvalidSession(HttpSession session) {
		session.setAttribute("fail", "Invalid Session");
		return REDIRECT + LOGIN;
	}

	private int generateOtp() {
		return RANDOM.nextInt(900000) + 100000;
	}

	/* ================= REGISTER ================= */
	public String loadRegister(ModelMap map, User user) {
		map.put("user", user);
		return "register.html";
	}

	public String register(User user, BindingResult result, HttpSession session) {
		if (!user.getPassword().equals(user.getConfirmpassword()))
			result.rejectValue("confirmpassword", "error.confirmpassword", "Password Not Matching");

		if (userRepository.existsByEmail(user.getEmail()))
			result.rejectValue("email", "error.email", "Email already Exists");

		if (userRepository.existsByMobile(user.getMobile()))
			result.rejectValue("mobile", "error.mobile", "Mobile Already Exists");

		if (userRepository.existsByUsername(user.getUsername()))
			result.rejectValue("username", "error.username", "Username Taken");

		if (result.hasErrors())
			return "register.html";

		user.setPassword(AES.encrypt(user.getPassword()));
		int otp = generateOtp();
		user.setOtp(otp);
		logger.info("OTP: {}", otp);

		userRepository.save(user);
		session.setAttribute("pass", "OTP Sent Success");

		return REDIRECT + "otp/" + user.getId();
	}

	public String verifyOtp(int id, int otp, HttpSession session) {
		Optional<User> optUser = userRepository.findById(id);
		if (optUser.isEmpty())
			return REDIRECT + LOGIN;

		User user = optUser.get();
		if (user.getOtp() == otp) {
			user.setVerified(true);
			user.setOtp(0);
			userRepository.save(user);
			session.setAttribute("pass", "Account Created Success");
			return REDIRECT + LOGIN;
		}

		session.setAttribute("fail", "Invalid OTP");
		return REDIRECT + "otp/" + id;
	}

	public String resendOtp(int id, HttpSession session) {
		Optional<User> optUser = userRepository.findById(id);
		if (optUser.isEmpty())
			return REDIRECT + LOGIN;

		User user = optUser.get();
		user.setOtp(generateOtp());
		userRepository.save(user);
		session.setAttribute("pass", "OTP Sent Success");

		return REDIRECT + "otp/" + user.getId();
	}

	/* ================= LOGIN / LOGOUT ================= */
	public String login(String username, String password, HttpSession session) {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			session.setAttribute("fail", "Invalid Username");
			return REDIRECT + LOGIN;
		}

		if (!AES.decrypt(user.getPassword()).equals(password)) {
			session.setAttribute("fail", "Incorrect Password");
			return REDIRECT + LOGIN;
		}

		if (!user.isVerified()) {
			user.setOtp(generateOtp());
			userRepository.save(user);
			session.setAttribute("pass", "Verify Email First");
			return REDIRECT + "otp/" + user.getId();
		}

		session.setAttribute("user", user);
		session.setAttribute("pass", "Login Success");
		return REDIRECT + "home";
	}

	public String logout(HttpSession session) {
		session.removeAttribute("user");
		session.setAttribute("pass", "Logout Success");
		return REDIRECT + LOGIN;
	}

	/* ================= HOME ================= */
	public String loadHome(HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		List<Post> posts = postRepository.findByUserIn(user.getFollowing());
		map.put("user", user);
		map.put(POSTS, posts);
		return "home.html";
	}

	/* ================= PROFILE ================= */
	public String profile(HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		user = userRepository.findById(user.getId()).orElse(null);
		session.setAttribute("user", user);

		List<Post> posts = postRepository.findByUser(user);
		if (!posts.isEmpty())
			map.put("posts", posts);

		map.put("user", user);
		return "profile.html";
	}

	public String editProfile(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);
		return "edit-profile.html";
	}

	public String updateProfile(MultipartFile image, HttpSession session, String bio) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		user.setBio(bio);
		if (image != null && !image.isEmpty()) {
			user.setImageUrl(cloudinaryHelper.saveImage(image));
		}
		userRepository.save(user);
		session.setAttribute("user", user);
		return REDIRECT + PROFILE;
	}

	/* ================= POST HANDLING ================= */
	public String loadAddPost(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);
		return "addpost.html";
	}

	public String addPost(Post post, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		post.setUser(user);
		post.setImageUrl(cloudinaryHelper.saveImage(post.getImage()));
		postRepository.save(post);

		session.setAttribute("pass", "Posted Success");
		return REDIRECT + PROFILE;
	}

	public String deletePost(int id, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		postRepository.deleteById(id);
		session.setAttribute("pass", "Post Deleted");
		return REDIRECT + PROFILE;
	}

	public String editPost(HttpSession session, int id, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		postRepository.findById(id).ifPresent(post -> map.put("post", post));
		return "edit-post.html";
	}

	public String updatePost(Post post, HttpSession session) throws IOException {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		postRepository.findById(post.getId()).ifPresent(existing -> {
			if (post.getImage() != null && !post.getImage().isEmpty()) {
				post.setImageUrl(cloudinaryHelper.saveImage(post.getImage()));
			} else {
				post.setImageUrl(existing.getImageUrl());
			}
		});

		post.setUser(user);
		postRepository.save(post);
		session.setAttribute("pass", "Updated Success");
		return REDIRECT + PROFILE;
	}

	/* ================= FOLLOW / UNFOLLOW ================= */
	public String viewSuggestions(ModelMap map, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		List<User> suggestions = userRepository.findByVerifiedTrue();
		suggestions.removeIf(u -> u.getId() == user.getId() || user.getFollowers().contains(u));

		if (suggestions.isEmpty()) {
			session.setAttribute("fail", "No Suggestions");
			return REDIRECT + PROFILE;
		}

		map.put("suggestions", suggestions);
		return "suggestions.html";
	}

	public String followUser(int id, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		userRepository.findById(id).ifPresent(followed -> {
			user.getFollowing().add(followed);
			followed.getFollowers().add(user);
			userRepository.save(user);
			userRepository.save(followed);
		});

		session.setAttribute("user", user);
		return REDIRECT + PROFILE;
	}

	public String unfollow(HttpSession session, int id) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		userRepository.findById(id).ifPresent(toUnfollow -> {
			user.getFollowing().remove(toUnfollow);
			toUnfollow.getFollowers().remove(user);
			userRepository.save(user);
			userRepository.save(toUnfollow);
		});

		session.setAttribute("user", user);
		return REDIRECT + PROFILE;
	}

	public String getFollowers(HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		map.put("followers", user.getFollowers());
		return "followers.html";
	}

	public String getFollowing(HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		map.put("following", user.getFollowing());
		return "following.html";
	}

	public String viewProfile(int id, HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		userRepository.findById(id).ifPresent(profileUser -> {
			List<Post> posts = postRepository.findByUser(profileUser);
			map.put(POSTS, posts);
			map.put("user", profileUser);
		});
		return "view-profile.html";
	}

	/* ================= LIKE / COMMENT ================= */
	public String likePost(int id, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		postRepository.findById(id).ifPresent(post -> {
			if (post.getLikedUsers().stream().noneMatch(u -> u.getId() == user.getId()))
				post.getLikedUsers().add(user);
			postRepository.save(post);
		});

		return REDIRECT + "home";
	}

	public String dislikePost(int id, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		postRepository.findById(id).ifPresent(post -> {
			post.getLikedUsers().removeIf(u -> u.getId() == user.getId());
			postRepository.save(post);
		});

		return REDIRECT + "home";
	}

	public String loadCommentPage(HttpSession session, int id, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		map.put("id", id);
		return "comment.html";
	}

	public String comment(HttpSession session, int id, String comment) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		postRepository.findById(id).ifPresent(post -> {
			Comment userComment = new Comment();
			userComment.setComment(comment);
			userComment.setUser(user);
			post.getComments().add(userComment);
			postRepository.save(post);
		});

		return REDIRECT + "home";
	}

	/* ================= PRIME ================= */
	public String prime(HttpSession session, ModelMap map) throws RazorpayException {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);
		JSONObject object = new JSONObject();
		object.put(AMOUNT, 19900);
		object.put(CURRENCY, "INR");

		Order order = client.orders.create(object);

		map.put("key", razorpayKey);
		map.put(AMOUNT, order.get(AMOUNT));
		map.put(CURRENCY, order.get(CURRENCY));
		map.put("orderId", order.get("id"));
		map.put("user", user);

		return "payment.html";
	}

	public String prime(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null)
			return handleInvalidSession(session);

		user.setPrime(true);
		userRepository.save(user);
		session.setAttribute("user", user);

		return REDIRECT + PROFILE;
	}
}
