package org.jsp.jsp_gram.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OtpRequest {

	private Integer userId;
	private Integer otp;

}