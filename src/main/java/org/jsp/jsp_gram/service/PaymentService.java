package org.jsp.jsp_gram.service;

import org.json.JSONObject;
import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.PaymentResponse;
import org.jsp.jsp_gram.dto.PaymentStatusResponse;
import org.jsp.jsp_gram.dto.VerifyPaymentRequest;
import org.jsp.jsp_gram.model.User;
import org.jsp.jsp_gram.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	@Value("${razorpay.key}")
	private String razorpayKey;

	@Value("${razorpay.secret}")
	private String razorpaySecret;

	private final CurrentUserService currentUserService;
	private final UserRepository userRepository;

	/**
	 * Create Razorpay Order.
	 */
	public ApiResponse<PaymentResponse> createOrder() throws Exception {

		currentUserService.getLoggedInUser();

		RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);

		JSONObject object = new JSONObject();
		object.put("amount", 19900);
		object.put("currency", "INR");

		Order order = client.orders.create(object);

		PaymentResponse response = new PaymentResponse(order.get("id"), order.get("amount"), order.get("currency"),
				razorpayKey);

		return new ApiResponse<>(true, "Order created successfully", response);
	}

	/**
	 * Verify payment.
	 */

	@Transactional
	public ApiResponse<Void> verifyPayment(VerifyPaymentRequest request) {

		User user = currentUserService.getLoggedInUser();

		// TODO: Verify Razorpay signature before updating user

		user.setPrime(true);

		userRepository.save(user);

		return new ApiResponse<>(true, "Payment verified successfully");
	}

	/**
	 * Get payment status.
	 */

	@Transactional(readOnly = true)
	public ApiResponse<PaymentStatusResponse> getPaymentStatus() {

		User user = currentUserService.getLoggedInUser();

		PaymentStatusResponse response = new PaymentStatusResponse(user.isPrime());

		return new ApiResponse<>(true, "Payment status fetched successfully", response);
	}
}