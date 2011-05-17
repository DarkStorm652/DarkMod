package org.darkstorm.tools.exceptions;

@SuppressWarnings("serial")
public class IncorrectConditionsException extends RuntimeException {

	public IncorrectConditionsException() {
		super();
	}

	public IncorrectConditionsException(String message) {
		super(message);
	}

	public IncorrectConditionsException(Throwable cause) {
		super(cause);
	}

	public IncorrectConditionsException(String message, Throwable cause) {
		super(message, cause);
	}

}
