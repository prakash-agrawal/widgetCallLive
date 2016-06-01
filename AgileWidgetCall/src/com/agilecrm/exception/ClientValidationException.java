package com.agilecrm.exception;

public class ClientValidationException extends AgileApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8252601251717354336L;

	public ClientValidationException() {
		super();
	}

	public ClientValidationException(String message) {
		super(message);
	}

	public ClientValidationException(Throwable cause) {
		super(cause);
	}

	public ClientValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClientValidationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
