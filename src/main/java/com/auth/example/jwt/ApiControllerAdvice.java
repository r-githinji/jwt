package com.auth.example.jwt;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ApiControllerAdvice {
	
	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public ResponseEntity<List<Growl>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
		List<Growl> growls = ex.getAllErrors().stream()
			.map(error -> Growl.of(error.getCode(), "error", error.getDefaultMessage())).collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(growls);
	}
	
	@ExceptionHandler(value = {Exceptions.PasswordsDoNotMatchException.class})
	public ResponseEntity<Growl> handlePasswordsDoNotMatch(Exceptions.PasswordsDoNotMatchException ex, WebRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getError());
	}
	
	@ExceptionHandler(value = {Exceptions.UsernameNotAvailableException.class})
	public ResponseEntity<Growl> handleUsernameNotAvailable(Exceptions.UsernameNotAvailableException ex, WebRequest request) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getError());
	}
}

