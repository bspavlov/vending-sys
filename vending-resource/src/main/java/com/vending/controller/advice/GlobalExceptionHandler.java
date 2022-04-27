package com.vending.controller.advice;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.vending.dto.MessageDto;
import com.vending.dto.ResponseCode;
import com.vending.dto.Violation;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<MessageDto> illegalArgsException(IllegalArgumentException e) {
		return new ResponseEntity<MessageDto>(
				MessageDto.builder().code(ResponseCode.IllegalArguments)
						.msg(e.getMessage()).details(ExceptionUtils.getStackTrace(e)).build(),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<MessageDto> illegalStgateException(IllegalStateException e) {
		return new ResponseEntity<MessageDto>(MessageDto.builder().code(ResponseCode.IllegalState)
				.msg(e.getMessage()).details(ExceptionUtils.getStackTrace(e)).build(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public MessageDto entityNotFound(EntityNotFoundException e) {
		return MessageDto.builder().code(ResponseCode.EntityNotFound).msg(e.getMessage())
				.details(ExceptionUtils.getStackTrace(e)).build();
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<MessageDto> runtimeException(RuntimeException e) {
		return new ResponseEntity<MessageDto>(
				MessageDto.builder().code(ResponseCode.Unspecified).msg(e.getMessage())
						.details(ExceptionUtils.getStackTrace(e)).build(),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<MessageDto> authenticationException(AuthenticationException e) {
		return new ResponseEntity<MessageDto>(
				MessageDto.builder().code(ResponseCode.Unauthenticated).msg(e.getMessage()).details("").build(),
				HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<MessageDto> accessDeniedException(AccessDeniedException e) {
		return new ResponseEntity<MessageDto>(
				MessageDto.builder().code(ResponseCode.Unauthorized).msg(e.getMessage())
						.details(ExceptionUtils.getStackTrace(e)).build(),
				HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public MessageDto requiredArgs(MethodArgumentNotValidException e) {
		List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
				.map(v -> Violation.builder().fieldName(v.getField()).message(v.getDefaultMessage()).build())
				.collect(Collectors.toList());
		return MessageDto.builder().code(ResponseCode.InputValidation).violations(violations).build();
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public MessageDto constraintValidation(ConstraintViolationException e) {
		List<Violation> violations = resolveConstraintViolations(e.getConstraintViolations());
		return MessageDto.builder().code(ResponseCode.InputValidation).violations(violations).build();
	}

	@ExceptionHandler(UnsupportedOperationException.class)
	@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
	@ResponseBody
	public MessageDto unsupportedOpperation(UnsupportedOperationException e) {
		return MessageDto.builder().code(ResponseCode.NotImplemented).msg(e.getMessage()).build();
	}

	@ExceptionHandler(TransactionSystemException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public MessageDto transactionSystemException(TransactionSystemException e) {
		return MessageDto.builder().code(ResponseCode.IllegalState).msg(e.getOriginalException().getMessage()).build();
	}

	private List<Violation> resolveConstraintViolations(Collection<ConstraintViolation<?>> constraintViolations) {
		return constraintViolations.stream()
				.map(v -> Violation.builder().fieldName(getParameterName(v)).message(v.getMessage()).build())
				.collect(Collectors.toList());
	}

	@SuppressWarnings("rawtypes")
	private String getParameterName(ConstraintViolation constraintViolation) {
		return StreamSupport.stream(constraintViolation.getPropertyPath().spliterator(), false)
				.filter(p -> p.getKind().equals(ElementKind.PARAMETER)).findFirst().map(Path.Node::getName)
				.orElse(null);
	}

}
