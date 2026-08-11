package org.jsp.jsp_gram.dto;

import java.util.List;

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
	private String mobile;
	private String gender;

	private String bio;
	private String imageUrl;

	private boolean prime;

	private int postCount;
	private int followersCount;
	private int followingCount;

	private List<PostResponse> posts;
}