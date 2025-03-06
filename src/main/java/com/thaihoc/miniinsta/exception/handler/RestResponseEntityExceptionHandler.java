package com.thaihoc.miniinsta.exception.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.thaihoc.miniinsta.exception.CategoryNotFoundException;
import com.thaihoc.miniinsta.exception.CommentNotFoundException;
import com.thaihoc.miniinsta.exception.InvalidInputException;
import com.thaihoc.miniinsta.exception.NoPermissionException;
import com.thaihoc.miniinsta.exception.PostNotFoundException;
import com.thaihoc.miniinsta.exception.UserNotFoundException;

@RestControllerAdvice()
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	private static final String ERROR_CODE_INTERNAL = "INTERNAL_ERROR";
	private static final Map<Class<? extends RuntimeException>, HttpStatus> EXCEPTION_TO_HTTP_STATUS_CODE = Map.of(
			UserNotFoundException.class, HttpStatus.NOT_FOUND,
			PostNotFoundException.class, HttpStatus.NOT_FOUND,
			CommentNotFoundException.class, HttpStatus.NOT_FOUND,
			NoPermissionException.class, HttpStatus.FORBIDDEN,
			CategoryNotFoundException.class, HttpStatus.NOT_FOUND,
			InvalidInputException.class, HttpStatus.BAD_REQUEST);

	private static final Map<Class<? extends RuntimeException>, String> EXCEPTION_TO_ERROR_CODE = Map.of(
			UserNotFoundException.class, "USER_NOT_FOUND",
			PostNotFoundException.class, "POST_NOT_FOUND",
			CommentNotFoundException.class, "COMMENT_NOT_FOUND",
			NoPermissionException.class, "NO_PERMISSION",
			CategoryNotFoundException.class, "CATEGORY_NOT_FOUND",
			InvalidInputException.class, "INVALID_INPUT");

	@ExceptionHandler()
	ResponseEntity<ApiExceptionResponse> handleUserNotFoundException(RuntimeException exception) {
		HttpStatus httpStatus = EXCEPTION_TO_HTTP_STATUS_CODE.getOrDefault(exception.getClass(),
				HttpStatus.INTERNAL_SERVER_ERROR);
		String errorCode = EXCEPTION_TO_ERROR_CODE.getOrDefault(exception.getClass(), ERROR_CODE_INTERNAL);

		final ApiExceptionResponse response = ApiExceptionResponse.builder().status(httpStatus).errorCode(errorCode)
				.build();

		return ResponseEntity.status(response.getStatus()).body(response);
	}

}
