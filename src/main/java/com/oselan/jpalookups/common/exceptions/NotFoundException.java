package com.oselan.jpalookups.common.exceptions;

/***
 * Thrown when attempting to find an object that is not there
 * @author Ahmad Hamid
 *
 */
public class NotFoundException extends BaseException {
	private static final long serialVersionUID = -8596472890741536409L;
 
	private static final String errorCode = "NOT_FOUND";
	
	public NotFoundException() {
		super("Expected item not found!"); 
		
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause); 
	}

	public NotFoundException(String message) {
		super(message); 
	}

	public NotFoundException(Throwable cause) {
		super(cause); 
	}

	public NotFoundException(Long id ) {
		super(  "Item not found : " + id);
	}

	@Override
	public String getErrorCode() { 
		  return errorCode;
	}

	 
}
