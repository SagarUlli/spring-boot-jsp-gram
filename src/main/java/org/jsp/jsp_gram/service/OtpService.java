package org.jsp.jsp_gram.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;

import org.jsp.jsp_gram.exception.AuthException;
import org.jsp.jsp_gram.helper.EmailSender;
import org.jsp.jsp_gram.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Email-verification OTPs live in Redis, not in the users table:
 * <ul>
 * <li>{@code otp:<userId>} - the code, expires on its own after the TTL</li>
 * <li>{@code otp:attempts:<userId>} - wrong guesses, to stop brute-forcing</li>
 * <li>{@code otp:cooldown:<userId>} - blocks "resend" spam</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

	private static final SecureRandom RANDOM = new SecureRandom();

	private final StringRedisTemplate redis;
	private final EmailSender emailSender;

	@Value("${app.otp.ttl-minutes:5}")
	private long ttlMinutes;

	@Value("${app.otp.max-attempts:5}")
	private int maxAttempts;

	@Value("${app.otp.resend-cooldown-seconds:60}")
	private long resendCooldownSeconds;

	@Value("${app.otp.email-enabled:false}")
	private boolean emailEnabled;

	private static String otpKey(int userId) {
		return "otp:" + userId;
	}

	private static String attemptsKey(int userId) {
		return "otp:attempts:" + userId;
	}

	private static String cooldownKey(int userId) {
		return "otp:cooldown:" + userId;
	}

	/**
	 * Creates a fresh OTP for the user (replacing any previous one) and delivers
	 * it.
	 *
	 * @return false if the resend cooldown is still running, in which case nothing
	 *         is generated and the previous OTP stays valid
	 */
	public boolean issueOtp(User user) {

		int userId = user.getId();

		// Atomic "set if absent" so two quick requests can't both pass the cooldown.
		Boolean acquired = redis.opsForValue().setIfAbsent(cooldownKey(userId), "1",
				Duration.ofSeconds(resendCooldownSeconds));

		if (!Boolean.TRUE.equals(acquired)) {
			return false;
		}

		String otp = String.valueOf(100000 + RANDOM.nextInt(900000)); // always 6 digits, no leading zero

		redis.opsForValue().set(otpKey(userId), otp, Duration.ofMinutes(ttlMinutes));
		redis.delete(attemptsKey(userId));

		deliver(user, otp);

		return true;
	}

	/**
	 * Checks the submitted OTP. A correct OTP is consumed (single use).
	 *
	 * @return true if correct, false if wrong
	 * @throws AuthException if there is no valid OTP or too many wrong attempts
	 */
	public boolean verifyOtp(int userId, int submittedOtp) {

		String expected = redis.opsForValue().get(otpKey(userId));

		if (expected == null) {
			throw new AuthException("OTP expired. Please request a new one");
		}

		Long attempts = redis.opsForValue().increment(attemptsKey(userId));

		if (attempts != null && attempts == 1) {
			redis.expire(attemptsKey(userId), Duration.ofMinutes(ttlMinutes));
		}

		if (attempts != null && attempts > maxAttempts) {
			redis.delete(otpKey(userId));
			throw new AuthException("Too many wrong attempts. Please request a new OTP");
		}

		boolean matches = MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
				String.valueOf(submittedOtp).getBytes(StandardCharsets.UTF_8));

		if (matches) {
			redis.delete(List.of(otpKey(userId), attemptsKey(userId), cooldownKey(userId)));
		}

		return matches;
	}

	private void deliver(User user, String otp) {

		if (emailEnabled) {
			try {
				emailSender.sendOtp(user.getEmail(), Integer.parseInt(otp), user.getFirstname());
			} catch (Exception e) {
				log.error("Failed to send OTP email to user {}", user.getId(), e);
			}
		} else {
			// Local development only: there is no mail server to read the code from.
			log.warn("[DEV ONLY] OTP for user {} ({}) is {} - set app.otp.email-enabled=true to email it",
					user.getId(), user.getUsername(), otp);
		}
	}
}
