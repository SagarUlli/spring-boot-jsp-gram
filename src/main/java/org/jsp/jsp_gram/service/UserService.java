package org.jsp.jsp_gram.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.AuthResponse;
import org.jsp.jsp_gram.dto.RegisterRequest;
import org.jsp.jsp_gram.dto.UserResponse;
import org.jsp.jsp_gram.exception.AuthException;
import org.jsp.jsp_gram.helper.AES;
import org.jsp.jsp_gram.mapper.UserMapper;
import org.jsp.jsp_gram.model.User;
import org.jsp.jsp_gram.repository.UserRepository;
import org.jsp.jsp_gram.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final CurrentUserService currentUserService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final OtpService otpService;

	/* ================= REGISTER ================= */
	@Transactional
	public ApiResponse<Integer> registerRest(RegisterRequest request) {

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

		user.setPassword(passwordEncoder.encode(request.getPassword()));

		userRepository.save(user);

		otpService.issueOtp(user);

		return new ApiResponse<>(true, "OTP sent successfully", user.getId());
	}

	@Transactional
	public ApiResponse<Void> verifyOtpRest(Integer id, Integer otp) {

		if (id == null || otp == null) {
			return new ApiResponse<>(false, "User id and OTP are required");
		}

		User user = userRepository.findById(id).orElse(null);

		if (user == null) {
			return new ApiResponse<>(false, "User not found");
		}

		if (user.isVerified()) {
			return new ApiResponse<>(false, "Account is already verified");
		}

		if (!otpService.verifyOtp(user.getId(), otp)) {
			return new ApiResponse<>(false, "Invalid OTP");
		}

		user.setVerified(true);

		userRepository.save(user);

		return new ApiResponse<>(true, "Account verified successfully");
	}

	@Transactional(readOnly = true)
	public ApiResponse<Void> resendOtpRest(Integer userId) {

		if (userId == null) {
			return new ApiResponse<>(false, "User id is required");
		}

		User user = userRepository.findById(userId).orElseThrow(() -> new AuthException("User not found"));

		if (user.isVerified()) {
			return new ApiResponse<>(false, "Account is already verified");
		}

		if (!otpService.issueOtp(user)) {
			return new ApiResponse<>(false, "Please wait a minute before requesting another OTP");
		}

		return new ApiResponse<>(true, "OTP sent successfully");
	}

	/* ================= LOGIN / LOGOUT ================= */
	@Transactional
	public ApiResponse<AuthResponse> loginRest(String username, String password) {

		User user = userRepository.findByUsername(username);

		if (user == null) {
			throw new AuthException("User not found");
		}

		if (!passwordMatches(user, password)) {
			return new ApiResponse<>(false, "Incorrect Password");
		}

		if (!user.isVerified()) {
			// If an OTP was sent very recently the existing one is still valid.
			otpService.issueOtp(user);

			return new ApiResponse<>(false, "Verify Email First");
		}

		UserResponse userResponse = UserMapper.toResponse(user);

		AuthResponse response = new AuthResponse(jwtService.generateToken(user), "Bearer",
				jwtService.getExpirationSeconds(), userResponse);

		return new ApiResponse<>(true, "Login Success", response);
	}

	/**
	 * JWTs are stateless, so there is nothing to invalidate on the server: the
	 * client logs out by discarding its token.
	 */
	public ApiResponse<Void> logoutRest() {

		return new ApiResponse<>(true, "Logout Successful");
	}

	public ApiResponse<List<UserResponse>> searchUsers(String username) {

		currentUserService.getLoggedInUser();

		if (username == null || username.isBlank()) {
			return new ApiResponse<>(true, "Users fetched successfully", List.of());
		}

		List<UserResponse> users = userRepository.findByUsernameContainingIgnoreCaseAndVerifiedTrue(username).stream()
				.map(UserMapper::toResponse).toList();

		return new ApiResponse<>(true, "Users fetched successfully", users);
	}

	/**
	 * Checks the password against BCrypt. Accounts created before the Spring
	 * Security migration still hold the old reversible AES value; those are
	 * verified the old way once and immediately re-saved as BCrypt, so the legacy
	 * format disappears as users log in.
	 */
	private boolean passwordMatches(User user, String rawPassword) {

		String stored = user.getPassword();

		if (stored == null || rawPassword == null) {
			return false;
		}

		if (stored.startsWith("$2")) {
			return passwordEncoder.matches(rawPassword, stored);
		}

		String legacyPlain = AES.decrypt(stored);

		if (legacyPlain != null && legacyPlain.equals(rawPassword)) {
			user.setPassword(passwordEncoder.encode(rawPassword));
			userRepository.save(user);
			return true;
		}

		return false;
	}

}
