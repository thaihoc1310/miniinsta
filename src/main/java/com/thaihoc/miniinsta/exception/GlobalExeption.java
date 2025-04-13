package com.thaihoc.miniinsta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.thaihoc.miniinsta.dto.RestResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExeption {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<Object>> handleAllException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setMessage(ex.getMessage());
        res.setError("Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            IdInvalidException.class })

    public ResponseEntity<RestResponse<Object>> handleBadCredentialsException(Exception ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(ex.getMessage());
        res.setError("Exception occurs...");
        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleNoResourceFoundException(
            NoResourceFoundException noResourceFoundException) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setMessage(noResourceFoundException.getMessage());
        res.setError("404 Not Found. URL may not exist");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException methodArgumentNotValidException) {
        BindingResult result = methodArgumentNotValidException.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(methodArgumentNotValidException.getBody().getDetail());
        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage())
                .collect(Collectors.toList());
        res.setMessage(errors.size() > 1 ? errors : errors.get(0));
        return ResponseEntity.badRequest().body(res);
    }

    // @ExceptionHandler(value = {
    // StorageException.class })

    // public ResponseEntity<RestResponse<Object>>
    // handleFileUploadException(Exception ex) {
    // RestResponse<Object> res = new RestResponse<>();
    // res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    // res.setMessage(ex.getMessage());
    // res.setError("Exception upload file...");
    // return ResponseEntity.badRequest().body(res);
    // }

    @ExceptionHandler(value = {
            PermissionDeniedException.class })

    public ResponseEntity<RestResponse<Object>> handlePermissionException(Exception ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.FORBIDDEN.value());
        res.setMessage(ex.getMessage());
        res.setError("Forbidden access...");
        return ResponseEntity.badRequest().body(res);
    }
}
