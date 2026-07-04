package org.jsp.jsp_gram.exception;

import org.jsp.jsp_gram.dto.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ApiResponse<Void> handleAuth(AuthException ex) {

        return new ApiResponse<>(
                false,
                ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception ex) {

        ex.printStackTrace();

        return new ApiResponse<>(
                false,
                "Something went wrong."
		);
	}
}