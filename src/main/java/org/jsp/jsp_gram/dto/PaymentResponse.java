package org.jsp.jsp_gram.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

	private String orderId;
	private int amount;
	private String currency;
	private String key;
}