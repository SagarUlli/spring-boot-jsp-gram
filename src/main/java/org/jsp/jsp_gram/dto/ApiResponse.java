package org.jsp.jsp_gram.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

	private boolean success;
	private String message;
	private T data;
	private Map<String, String> errors;

	public ApiResponse(boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public ApiResponse(boolean success, String message, T data) {
		this.success = success;
		this.message = message;
		this.data = data;
	}

	public ApiResponse(boolean success, String message, Map<String, String> errors) {
		this.success = success;
		this.message = message;
		this.errors = errors;
	}

}