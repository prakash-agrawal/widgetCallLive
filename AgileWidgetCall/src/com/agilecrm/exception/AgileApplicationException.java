package com.agilecrm.exception;

public class AgileApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6722640681213609517L;

	public AgileApplicationException() {
		super();
	}

	public AgileApplicationException(String message) {
		super(message);
	}

	public AgileApplicationException(Throwable cause) {
		super(cause);
	}

	public AgileApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AgileApplicationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
