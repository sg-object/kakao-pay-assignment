package com.sg.data.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import com.sg.data.common.model.ExceptionResponse;

@Service
public class MessageService {

	@Autowired
	private MessageSourceAccessor messageSourceAccessor;

	public ExceptionResponse getExceptionResponse(String errorCode) {
		return new ExceptionResponse(errorCode, messageSourceAccessor.getMessage(errorCode));
	}
}
