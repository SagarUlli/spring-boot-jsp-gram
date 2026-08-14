package org.jsp.jsp_gram.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.RegisterRequest;
import org.jsp.jsp_gram.dto.UserResponse;
import org.jsp.jsp_gram.exception.AuthException;
import org.jsp.jsp_gram.helper.AES;
import org.jsp.jsp_gram.mapper.UserMapper;
import org.jsp.jsp_gram.model.User;
import org.jsp.jsp_gram.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private static final String REDIRECT = "redirect:/";
	private static final String LOGIN = "login";
	private final SessionService sessionService;

	@Value("${razorpay.key}")
	private String razorpayKey;

	@Value("${razorpay.secret}")
	private String razorpaySecret;

	private static final Random RANDOM = new Random();

	private final UserRepository userRepository;
//	private final EmailSender emailSender;

	/* ================= SESSION HANDLING ================= */
	private int generateOtp() {
		return RANDOM.nextInt(900000) + 100000;
	}

	/* ================= REGISTER ================= */
	public ApiResponse<Integer> registerRest(RegisterRequest request, HttpSession session) {

		Map<String, String> errors = new HashMap<>();

		if (!request.getPassword().equals(request.getConfirmPassword())) {
			errors.put("confirmPassword", "Passwords do not match");
		}

		if (userRepository.existsByEmail(request.getEmail())) {
			errors.put("email", "Email already exists");
		}

		if (userRepository.existsByMobile(request.getMobile())) {
			errors.put("mobile", "Mobile already exists");
		}

		if (userRepository.existsByUsername(request.getUsername())) {
			errors.put("username", "Username already exists");
		}

		if (!errors.isEmpty()) {
			return new ApiResponse<>(false, "Validation failed", errors);
		}

		User user = new User();

		user.setFirstname(request.getFirstname());
		user.setLastname(request.getLastname());
		user.setEmail(request.getEmail());
		user.setMobile(request.getMobile());
		user.setGender(request.getGender());
		user.setUsername(request.getUsername());

		user.setPassword(AES.encrypt(request.getPassword()));

		int otp = generateOtp();
		user.setOtp(otp);

		userRepository.save(user);

		session.setAttribute("pass", "OTP Sent Success");

		return new ApiResponse<>(true, "OTP sent successfully", user.getId());
	}

	public ApiResponse<Void> verifyOtpRest(int id, int otp, HttpSession session) {

		Optional<User> optUser = userRepository.findById(id);

		if (optUser.isEmpty()) {
			return new ApiResponse<>(false, "User not found");
		}

		User user = optUser.get();

		if (user.getOtp() != otp) {
			return new ApiResponse<>(false, "Invalid OTP");
		}

		user.setVerified(true);
		user.setOtp(0);

		userRepository.save(user);

		session.setAttribute("pass", "Account Created Success");

		return new ApiResponse<>(true, "Account verified successfully");
	}

	public ApiResponse<Void> resendOtpRest(int userId, HttpSession session) {

		User user = userRepository.findById(userId).orElseThrow(() -> new AuthException("User not found"));

		user.setOtp(generateOtp());

		userRepository.save(user);

		session.setAttribute("pass", "OTP Sent Success");

		return new ApiResponse<>(true, "OTP sent successfully");
	}

	/* ================= LOGIN / LOGOUT ================= */
	public ApiResponse<UserResponse> loginRest(
	        String username,
	        String password,
	        HttpSession session) {

	    User user = userRepository.findByUsername(username);

	    if (user == null) {
	        throw new AuthException("User not found");
	    }

	    if (!AES.decrypt(user.getPassword()).equals(password)) {
	        return new ApiResponse<>(false, "Incorrect Password");
	    }

	    if (!user.isVerified()) {

	        user.setOtp(generateOtp());
	        userRepository.save(user);

	        return new ApiResponse<>(false, "Verify Email First");
	    }

	    session.setAttribute("user", user);

	    UserResponse response = UserMapper.toResponse(user);

	    return new ApiResponse<>(
	            true,
	            "Login Success",
	            response
	    );
	}

	public String logout(HttpSession session) {
		session.removeAttribute("user");
		session.setAttribute("pass", "Logout Success");
		return REDIRECT + LOGIN;
	}

	public ApiResponse<Void> logoutRest(HttpSession session) {

		session.invalidate();

		return new ApiResponse<>(true, "Logout Successful");
	}

	public ApiResponse<List<UserResponse>> searchUsers(String username, HttpSession session) {

		sessionService.getLoggedInUser(session);

		if (username == null || username.isBlank()) {
			return new ApiResponse<>(true, "Users fetched successfully", List.of());
		}

		List<UserResponse> users = userRepository.findByUsernameContainingIgnoreCaseAndVerifiedTrue(username).stream()
				.map(UserMapper::toResponse).toList();

		return new ApiResponse<>(true, "Users fetched successfully", users);
	}

}
