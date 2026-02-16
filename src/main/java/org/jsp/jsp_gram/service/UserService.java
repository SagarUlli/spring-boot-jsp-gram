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

	private static final String REDIRECT = "redirect:/";
	private static final String AMOUNT = "amount";
	private static final String CURRENCY = "currency";
	private static final String LOGIN = "login";
	private static final String PROFILE = "profile";
	private static final String POSTS = "posts";
	private static final String USERID = "userId";

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

	/** Fetch current user by ID stored in session */
	private User getSessionUser(HttpSession session) {
		Integer userId = (Integer) session.getAttribute(USERID);
		return userId == null ? null : userRepository.findById(userId).orElse(null);
	}

	private String handleInvalidSession(HttpSession session) {
		session.setAttribute("fail", "Invalid Session");
		return REDIRECT + LOGIN;
	}

	private int generateOtp() {
		return RANDOM.nextInt(900000) + 100000;
	}

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
			result.rejectValue("mobile", "error.mobile", "Mobile Number Already Exists");

		if (userRepository.existsByUsername(user.getUsername()))
			result.rejectValue("username", "error.username", "Username already Taken");

		if (result.hasErrors())
			return "register.html";

		user.setPassword(AES.encrypt(user.getPassword()));
		int otp = generateOtp();
		user.setOtp(otp);
		emailSender.sendOtp(user.getEmail(), otp, user.getFirstname());
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
		session.setAttribute("pass", "Invalid OTP");
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
			session.setAttribute("pass", "Otp Sent Success, First Verify Email to Login");
			return REDIRECT + "otp/" + user.getId();
		}

		// store only user ID in session
		session.setAttribute(USERID, user.getId());
		session.setAttribute("pass", "Login Success");
		return REDIRECT + "home";
	}

	public String loadHome(HttpSession session, ModelMap map) {
		User user = getSessionUser(session);
		if (user == null)
			return handleInvalidSession(session);

		List<Post> posts = postRepository.findByUserIn(user.getFollowing());
		if (!posts.isEmpty())
			map.put(POSTS, posts);
		return "home.html";
	}

	public String logout(HttpSession session) {
		session.removeAttribute(USERID);
		session.setAttribute("pass", "Logout Success");
		return REDIRECT + LOGIN;
	}

	public String profile(HttpSession session, ModelMap map) {
		User user = getSessionUser(session);
		if (user == null)
			return handleInvalidSession(session);

		List<Post> posts = postRepository.findByUser(user);
		if (!posts.isEmpty())
			map.put(POSTS, posts);
		return "profile.html";
	}

	public String editProfile(HttpSession session) {
		return getSessionUser(session) != null ? "edit-profile.html" : handleInvalidSession(session);
	}

	public String updateProfile(MultipartFile image, HttpSession session, String bio) {
		User user = getSessionUser(session);
		if (user == null)
			return handleInvalidSession(session);

		user.setBio(bio);
		if (image != null && !image.isEmpty())
			user.setImageUrl(cloudinaryHelper.saveImage(image));
		userRepository.save(user);
		return REDIRECT + PROFILE;
	}

	public String loadAddPost(HttpSession session) {
		return getSessionUser(session) != null ? "addpost.html" : handleInvalidSession(session);
	}

	public String addPost(Post post, HttpSession session) {
		User user = getSessionUser(session);
		if (user == null)
			return handleInvalidSession(session);

		post.setUser(user);
		post.setImageUrl(cloudinaryHelper.saveImage(post.getImage()));
		postRepository.save(post);
		session.setAttribute("pass", "Posted Success");
		return REDIRECT + PROFILE;
	}

	public String deletePost(int id, HttpSession session) {
		postRepository.deleteById(id);
		session.setAttribute("pass", "Post Delete Success");
		return REDIRECT + PROFILE;
	}

	public String editPost(HttpSession session, int id, ModelMap map) {
		User user = getSessionUser(session);
		if (user == null)
			return handleInvalidSession(session);

		postRepository.findById(id).ifPresent(post -> map.put("post", post));
		return "edit-post.html";
	}

	public String updatePost(Post post, HttpSession session) throws IOException {
		User user = getSessionUser(session);
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

	public String viewSuggestions(ModelMap map, HttpSession session) {
		User user = getSessionUser(session);
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
		User user = getSessionUser(session);
		if (user == null)
			return handleInvalidSession(session);

		userRepository.findById(id).ifPresent(followed -> {
			user.getFollowing().add(followed);
			followed.getFollowers().add(user);
			userRepository.save(user);
			userRepository.save(followed);
			session.setAttribute(USERID, user.getId());
		});
		return REDIRECT + PROFILE;
	}

	public String unfollow(HttpSession session, int id) {
		User user = getSessionUser(session);
		if (user == null)
			return handleInvalidSession(session);

		userRepository.findById(id).ifPresent(toUnfollow -> {
			user.getFollowing().remove(toUnfollow);
			toUnfollow.getFollowers().remove(user);
			userRepository.save(user);
			userRepository.save(toUnfollow);
			session.setAttribute(USERID, user.getId());
		});
		return REDIRECT + PROFILE;
	}

	public String getFollowers(HttpSession session, ModelMap map) {
		User user = getSessionUser(session);
		if (user == null)
			return handleInvalidSession(session);

		List<User> followers = user.getFollowers();
		if (followers.isEmpty()) {
			session.setAttribute("fail", "No Followers");
			return REDIRECT + PROFILE;
		}

		map.put("followers", followers);
		return "followers.html";
	}

	public String getFollowing(HttpSession session, ModelMap map) {
		User user = getSessionUser(session);
		if (user == null)
			return handleInvalidSession(session);

		List<User> following = user.getFollowing();
		if (following.isEmpty()) {
			session.setAttribute("fail", "Not Following Anyone");
			return REDIRECT + PROFILE;
		}

		map.put("following", following);
		return "following.html";
	}

	public String viewProfile(int id, HttpSession session, ModelMap map) {
		User user = getSessionUser(session);
		if (user == null)
			return handleInvalidSession(session);

		userRepository.findById(id).ifPresent(profileUser -> {
			List<Post> posts = postRepository.findByUser(profileUser);
			if (!posts.isEmpty())
				map.put(POSTS, posts);
			map.put("user", profileUser);
		});
		return "view-profile.html";
	}

	public String likePost(int id, HttpSession session) {
		User user = getSessionUser(session);
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
		User user = getSessionUser(session);
		if (user == null)
			return handleInvalidSession(session);

		postRepository.findById(id).ifPresent(post -> {
			post.getLikedUsers().removeIf(u -> u.getId() == user.getId());
			postRepository.save(post);
		});
		return REDIRECT + "home";
	}

	public String loadCommentPage(HttpSession session, int id, ModelMap map) {
		User user = getSessionUser(session);
		if (user == null)
			return handleInvalidSession(session);

		map.put("id", id);
		return "comment.html";
	}

	public String comment(HttpSession session, int id, String comment) {
		User user = getSessionUser(session);
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

	public String prime(HttpSession session, ModelMap map) throws RazorpayException {
		User user = getSessionUser(session);
		if (user == null)
			return handleInvalidSession(session);

		RazorpayClient client = new RazorpayClient("rzp_test_6Lg2WKKGqBxoM2", "dVaKTcvZ8bMdDAPSuLGBkzUa");
		JSONObject object = new JSONObject();
		object.put(AMOUNT, 19900);
		object.put(CURRENCY, "INR");
		Order order = client.orders.create(object);

		map.put("key", "rzp_test_6Lg2WKKGqBxoM2");
		map.put(AMOUNT, order.get(AMOUNT));
		map.put(CURRENCY, order.get(CURRENCY));
		map.put("orderId", order.get("id"));
		map.put("user", user);

		return "payment.html";
	}

	public String prime(HttpSession session) {
		User user = getSessionUser(session);
		if (user == null)
			return handleInvalidSession(session);

		user.setPrime(true);
		userRepository.save(user);
		session.setAttribute(USERID, user.getId());
		return REDIRECT + PROFILE;
	}
}
