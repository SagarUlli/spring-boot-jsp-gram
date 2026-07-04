package org.jsp.jsp_gram.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

	private Integer id;
	private String firstname;
	private String lastname;
	private String username;
	private String email;
	private long mobile;
	private String gender;
	private String imageUrl;
	private boolean prime;
}