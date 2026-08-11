package org.jsp.jsp_gram.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

	private String firstname;
	private String lastname;
	private String email;
	private String mobile;
	private String gender;
	private String username;
	private String password;
	private String confirmPassword;

}