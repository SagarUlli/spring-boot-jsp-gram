package org.jsp.jsp_gram.controller;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.jsp.jsp_gram.dto.PaymentResponse;
import org.jsp.jsp_gram.dto.PaymentStatusResponse;
import org.jsp.jsp_gram.dto.VerifyPaymentRequest;
import org.jsp.jsp_gram.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentRestController {

	private final PaymentService paymentService;

	@PostMapping("/create-order")
	public ApiResponse<PaymentResponse> createOrder() throws Exception {

		return paymentService.createOrder();
	}

	@PostMapping("/verify")
	public ApiResponse<Void> verifyPayment(@RequestBody VerifyPaymentRequest request) {

		return paymentService.verifyPayment(request);
	}

	@GetMapping("/status")
	public ApiResponse<PaymentStatusResponse> getPaymentStatus() {

		return paymentService.getPaymentStatus();
	}
}