package ru.rayyxd.aetpreparation.exceptions;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ru.rayyxd.aetpreparation.responses.AppBaseResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler
	public ResponseEntity<AppBaseResponse> catchBadCredentialsException(BadCredentialsException e){
		System.out.println(e.getMessage());
		return new ResponseEntity<>(new AppBaseResponse(false, "Your email or password is incorrect"), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler
	public ResponseEntity<AppBaseResponse> catchForbiddenEmailException(ForbiddenEmailException e){
		System.out.println(e.getMessage());
		return new ResponseEntity<>(new AppBaseResponse(false, "This email already exists"), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler
	public ResponseEntity<List<AppBaseResponse>> catchFieldValidationException(FieldValidationException e) {
	    List<AppBaseResponse> errors = e.getBindingResult().getFieldErrors().stream()
	        .map(err -> new AppBaseResponse(false, err.getDefaultMessage()))
	        .toList();
	    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler
	public ResponseEntity<AppBaseResponse> catchInvalidCredentialsException(InvalidCredentialsException e){
		System.out.println(e.getMessage());
		return new ResponseEntity<>(new AppBaseResponse(false, e.getMessage()), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler
	public ResponseEntity<AppBaseResponse> catchNoAuthTokenException(NoAuthTokenException e){
		System.out.println(e.getMessage());
		return new ResponseEntity<>(new AppBaseResponse(false, e.getMessage()), HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler
	public ResponseEntity<AppBaseResponse> catchNoSuchElementException(NoSuchElementException e){
		System.out.println(e.getMessage());
		return new ResponseEntity<>(new AppBaseResponse(false,"Element not found"), HttpStatus.NOT_FOUND);
		
	}
}
