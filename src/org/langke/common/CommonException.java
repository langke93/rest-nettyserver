package org.langke.common;

/*
 * @copyright (c) langke 2011 
 * @author langke    2011-5-1 
 */
/**
 *  知道明确的错误原因的异常
 */
public class CommonException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public int errorCode = 502;
	public CommonException(String message) {
		super(message);
	}

	public CommonException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CommonException(String message,int errcode) {
		super(message);
		this.errorCode=errcode;
	}

	public CommonException(String message, Throwable cause,int errcode) {
		super(message, cause);
	    this.errorCode=errcode;
	}
}
