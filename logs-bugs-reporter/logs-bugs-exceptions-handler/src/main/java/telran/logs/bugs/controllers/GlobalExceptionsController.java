package telran.logs.bugs.controllers;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import telran.logs.bugs.exceptions.*;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionsController {
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String constraintViolationHandler(ConstraintViolationException e) {
		return processingExceptions(e);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String methodArgumentNotValidException(MethodArgumentNotValidException e) {
		return processingExceptions(e);
	}
	@ExceptionHandler(WebExchangeBindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String webExchangeBindException(WebExchangeBindException e) {
		return processingExceptions(e);
	}
	@ExceptionHandler(DuplicatedException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	String duplicatedKeyHandler(DuplicatedException e) {
		return processingExceptions(e);
	}
	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String notFounHandler(NotFoundException e) {
		return processingExceptions(e);
	}
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	String serverExceptionHandler(RuntimeException e) {
		return processingExceptions(e);
	}

	private String processingExceptions(Exception e) {
		 log.error("excepion class: {}, message: {}", e.getClass().getSimpleName(), e.getMessage());
		return e.getMessage();
	}
}
