package com.oselan.jpalookups.common.exceptions;

 
/***
 * A conflict exception is thrown when a process could not be completed because it would result in an unexpected data situation.
 * @author Ahmad Hamid
 *
 */
public class ConflictException extends BaseException {
	private static final long serialVersionUID = -8596472890741536409L;
 
	private static final String errorCode = "CONFLICT";
	
	public ConflictException() {
		super("Unexpected data situation occurred."); 
	}

	public ConflictException(String message, Throwable cause) {
		super(message, cause); 
	}

	public ConflictException(String message) {
		super(message); 
	}

	public ConflictException(Throwable cause) {
		super(cause); 
	}

	 

	@Override
	public String getErrorCode() { 
		  return errorCode;
	}

	 
}
