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

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	@Value("${razorpay.key}")
	private String razorpayKey;

	@Value("${razorpay.secret}")
	private String razorpaySecret;

	private final SessionService sessionService;
	private final UserRepository userRepository;

	/**
	 * Create Razorpay Order.
	 */
	public ApiResponse<PaymentResponse> createOrder(HttpSession session) throws Exception {

		sessionService.getLoggedInUser(session);

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
	public ApiResponse<Void> verifyPayment(VerifyPaymentRequest request, HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		// TODO: Verify Razorpay signature before updating user

		user.setPrime(true);

		userRepository.save(user);

		session.setAttribute("user", user);

		return new ApiResponse<>(true, "Payment verified successfully");
	}

	/**
	 * Get payment status.
	 */
	public ApiResponse<PaymentStatusResponse> getPaymentStatus(HttpSession session) {

		User user = sessionService.getLoggedInUser(session);

		PaymentStatusResponse response = new PaymentStatusResponse(user.isPrime());

		return new ApiResponse<>(true, "Payment status fetched successfully", response);
	}
}