package org.darkstorm.tools.exceptions;

@SuppressWarnings("serial")
public class ToBeImplementedException extends RuntimeException {

	public ToBeImplementedException() {
	}

	public ToBeImplementedException(String message) {
		super(message);
	}

	public ToBeImplementedException(Throwable cause) {
		super(cause);
	}

	public ToBeImplementedException(String message, Throwable cause) {
		super(message, cause);
	}

}
