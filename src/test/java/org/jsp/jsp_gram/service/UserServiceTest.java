package org.jsp.jsp_gram.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jsp.jsp_gram.dto.Post;
import org.jsp.jsp_gram.dto.User;
import org.jsp.jsp_gram.helper.AES;
import org.jsp.jsp_gram.helper.CloudinaryHelper;
import org.jsp.jsp_gram.helper.EmailSender;
import org.jsp.jsp_gram.repository.PostRepository;
import org.jsp.jsp_gram.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private EmailSender emailSender;

	@Mock
	private CloudinaryHelper cloudinaryHelper;

	@Mock
	private PostRepository postRepository;

	@Mock
	private HttpSession session;

	private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		userService = new UserService(userRepository, emailSender, cloudinaryHelper, postRepository);
	}

	// ===== LOGOUT =====
	@Test
	void logout_shouldRemoveUserAndReturnLoginRedirect() {
		String result = userService.logout(session);

		verify(session).removeAttribute("userId");
		verify(session).setAttribute("pass", "Logout Success");
		assertEquals("redirect:/login", result);
	}

	// ===== LOGIN =====
	@Test
	void login_shouldFail_whenUserNotFound() {
		when(userRepository.findByUsername("unknown")).thenReturn(null);
		String result = userService.login("unknown", "1234", session);

		verify(session).setAttribute("fail", "Invalid Username");
		assertEquals("redirect:/login", result);
	}

	@Test
	void login_shouldFail_whenPasswordIncorrect() {
		User user = new User();
		user.setUsername("sagar");
		user.setPassword(AES.encrypt("correct123"));
		user.setVerified(true);
		when(userRepository.findByUsername("sagar")).thenReturn(user);

		String result = userService.login("sagar", "wrong123", session);

		verify(session).setAttribute("fail", "Incorrect Password");
		assertEquals("redirect:/login", result);
	}

	@Test
	void login_shouldSendOtp_whenUserNotVerified() {
		User user = new User();
		user.setId(1);
		user.setUsername("sagar");
		user.setPassword(AES.encrypt("correct123"));
		user.setVerified(false);
		when(userRepository.findByUsername("sagar")).thenReturn(user);

		String result = userService.login("sagar", "correct123", session);

		verify(userRepository).save(user);
		verify(session).setAttribute("pass", "Otp Sent Success, First Verify Email to Login");
		assertNotEquals(0, user.getOtp());
		assertEquals("redirect:/otp/1", result);
	}

	@Test
	void login_shouldRedirectHome_whenUserVerified() {
		User user = new User();
		user.setId(1);
		user.setUsername("sagar");
		user.setPassword(AES.encrypt("correct123"));
		user.setVerified(true);
		when(userRepository.findByUsername("sagar")).thenReturn(user);

		String result = userService.login("sagar", "correct123", session);

		verify(session).setAttribute("userId", user.getId());
		verify(session).setAttribute("pass", "Login Success");
		assertEquals("redirect:/home", result);
	}

	// ===== REGISTER =====
	@Test
	void register_shouldReturnRegister_whenPasswordMismatch() {
		User user = new User();
		user.setPassword("pass1");
		user.setConfirmpassword("pass2");
		BindingResult result = mock(BindingResult.class);

		when(result.hasErrors()).thenReturn(true);
		String view = userService.register(user, result, session);

		verify(result).rejectValue("confirmpassword", "error.confirmpassword", "Password Not Matching");
		assertEquals("register.html", view);
	}

	@Test
	void register_shouldRedirectToOtp_whenValid() {
		User user = new User();
		user.setId(1);
		user.setPassword("pass123");
		user.setConfirmpassword("pass123");
		user.setEmail("unique@example.com");
		user.setMobile(9999999999L);
		user.setUsername("newUser");

		BindingResult result = mock(BindingResult.class);
		when(result.hasErrors()).thenReturn(false);

		when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
		when(userRepository.existsByMobile(user.getMobile())).thenReturn(false);
		when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
		doNothing().when(emailSender).sendOtp(anyString(), anyInt(), anyString());
		when(userRepository.save(user)).thenReturn(user);

		String view = userService.register(user, result, session);

		verify(userRepository).save(user);
		verify(session).setAttribute("pass", "OTP Sent Success");
		assertEquals("redirect:/otp/1", view);
	}

	// ===== VERIFY OTP =====
	@Test
	void verifyOtp_shouldVerifyUser_whenOtpMatches() {
		User user = new User();
		user.setId(1);
		user.setOtp(123456);
		user.setVerified(false);

		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		when(userRepository.save(user)).thenReturn(user);

		String view = userService.verifyOtp(1, 123456, session);

		verify(userRepository).save(user);
		verify(session).setAttribute("pass", "Account Created Success");
		assertTrue(user.isVerified());
		assertEquals(0, user.getOtp());
		assertEquals("redirect:/login", view);
	}

	@Test
	void verifyOtp_shouldFail_whenOtpIncorrect() {
		User user = new User();
		user.setId(1);
		user.setOtp(123456);
		user.setVerified(false);

		when(userRepository.findById(1)).thenReturn(Optional.of(user));

		String view = userService.verifyOtp(1, 111111, session);

		verify(session).setAttribute("pass", "Invalid OTP");
		verify(userRepository, never()).save(user);
		assertFalse(user.isVerified());
		assertEquals(123456, user.getOtp());
		assertEquals("redirect:/otp1", view);
	}

	// ===== POSTS =====
	@Test
	void addPost_shouldSavePost_whenSessionValid() {
		User user = new User();
		user.setId(1);
		when(session.getAttribute("userId")).thenReturn(user.getId());
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		Post post = new Post();
		MultipartFile image = mock(MultipartFile.class);
		post.setImage(image);
		when(cloudinaryHelper.saveImage(image)).thenReturn("image-url");
		when(postRepository.save(post)).thenReturn(post);

		String view = userService.addPost(post, session);

		verify(postRepository).save(post);
		verify(session).setAttribute("pass", "Posted Success");
		assertEquals(user, post.getUser());
		assertEquals("image-url", post.getImageUrl());
		assertEquals("redirect:/profile", view);
	}

	@Test
	void addPost_shouldRedirectLogin_whenSessionInvalid() {
		when(session.getAttribute("userId")).thenReturn(null);

		Post post = new Post();
		String view = userService.addPost(post, session);

		verify(postRepository, never()).save(post);
		verify(session).setAttribute("fail", "Invalid Session");
		assertEquals("redirect:/login", view);
	}

	@Test
	void updatePost_shouldUpdateImage_whenNewImageProvided() throws IOException {
		User user = new User();
		user.setId(1);
		when(session.getAttribute("userId")).thenReturn(user.getId());
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		Post existingPost = new Post();
		existingPost.setId(1);
		existingPost.setImageUrl("old");
		when(postRepository.findById(1)).thenReturn(Optional.of(existingPost));

		Post postToUpdate = new Post();
		postToUpdate.setId(1);
		MultipartFile newImage = mock(MultipartFile.class);
		when(newImage.isEmpty()).thenReturn(false);
		postToUpdate.setImage(newImage);
		when(cloudinaryHelper.saveImage(newImage)).thenReturn("new");

		String view = userService.updatePost(postToUpdate, session);

		verify(postRepository).save(postToUpdate);
		verify(session).setAttribute("pass", "Updated Success");
		assertEquals("new", postToUpdate.getImageUrl());
		assertEquals(user, postToUpdate.getUser());
		assertEquals("redirect:/profile", view);
	}

	@Test
	void updatePost_shouldKeepOldImage_whenNoNewImage() throws IOException {
		User user = new User();
		user.setId(1);
		when(session.getAttribute("userId")).thenReturn(user.getId());
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		Post existingPost = new Post();
		existingPost.setId(1);
		existingPost.setImageUrl("old");
		when(postRepository.findById(1)).thenReturn(Optional.of(existingPost));

		Post postToUpdate = new Post();
		postToUpdate.setId(1);
		MultipartFile newImage = mock(MultipartFile.class);
		when(newImage.isEmpty()).thenReturn(true);
		postToUpdate.setImage(newImage);

		String view = userService.updatePost(postToUpdate, session);

		verify(postRepository).save(postToUpdate);
		verify(session).setAttribute("pass", "Updated Success");
		assertEquals("old", postToUpdate.getImageUrl());
		assertEquals(user, postToUpdate.getUser());
		assertEquals("redirect:/profile", view);
	}

	// ===== FOLLOW / UNFOLLOW =====
	@Test
	void followUser_shouldUpdateBothUsers() {
		User user = new User();
		user.setId(1);
		User followed = new User();
		followed.setId(2);

		when(session.getAttribute("userId")).thenReturn(user.getId());
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(userRepository.findById(2)).thenReturn(Optional.of(followed));

		String view = userService.followUser(2, session);

		verify(userRepository).save(user);
		verify(userRepository).save(followed);
		verify(session).setAttribute("userId", user.getId());
		assertTrue(user.getFollowing().contains(followed));
		assertTrue(followed.getFollowers().contains(user));
		assertEquals("redirect:/profile", view);
	}

	@Test
	void unfollow_shouldRemoveFollowedUser() {
		User user = new User();
		user.setId(1);
		User followed = new User();
		followed.setId(2);
		followed.setFollowers(new ArrayList<>(List.of(user)));
		user.setFollowing(new ArrayList<>(List.of(followed)));

		when(session.getAttribute("userId")).thenReturn(user.getId());
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		String view = userService.unfollow(session, 2);

		verify(userRepository).save(user);
		verify(userRepository).save(followed);
		verify(session).setAttribute("userId", user.getId());
		assertFalse(user.getFollowing().contains(followed));
		assertFalse(followed.getFollowers().contains(user));
		assertEquals("redirect:/profile", view);
	}

	// ===== PRIME =====
	@Test
	void primeWithoutPayment_shouldSetPrimeTrue() {
		User user = new User();
		user.setId(1);
		when(session.getAttribute("userId")).thenReturn(user.getId());
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		String view = userService.prime(session);

		verify(userRepository).save(user);
		verify(session).setAttribute("userId", user.getId());
		assertTrue(user.isPrime());
		assertEquals("redirect:/profile", view);
	}

}
