package com.oselan.commons.exceptions;

 
public abstract class BaseException extends Exception implements BusinessException{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errorCode = UNDEFINED_ERROR;

 
	public BaseException() {
		super();
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(Throwable cause) {
		super(cause);
	} 
 
	public String getErrorCode() {
		return errorCode;
	}

}
