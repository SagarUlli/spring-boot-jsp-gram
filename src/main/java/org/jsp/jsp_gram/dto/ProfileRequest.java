package org.jsp.jsp_gram.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ProfileRequest {

	private String firstname;
	private String lastname;
	private String bio;
	private MultipartFile image;
}