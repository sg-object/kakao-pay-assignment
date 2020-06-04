package com.sg.assignment.common.exception;

import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.sg.assignment.common.model.ExceptionResponse;
import com.sg.assignment.common.service.MessageService;

@RestControllerAdvice
public class ExceptionControllerAdvice {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MessageService messageService;

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionResponse handleInternalServerErrorException(HttpServletResponse response, Exception e) {
		logger.error("Internal Server Error Exception has caught.", e);
		return messageService.getExceptionResponse(AbstractException.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(AbstractException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionResponse handleException(HttpServletResponse response, AbstractException e) {
		logger.error("Exception has caught.", e);
		return messageService.getExceptionResponse(e.getErrorCode());
	}
}
