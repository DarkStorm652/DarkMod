package org.darkstorm.tools.exceptions;

@SuppressWarnings("serial")
public class NullArgumentException extends RuntimeException {

	public NullArgumentException() {
	}

	public NullArgumentException(String message) {
		super(message);
	}

	public NullArgumentException(Throwable cause) {
		super(cause);
	}

	public NullArgumentException(String message, Throwable cause) {
		super(message, cause);
	}

	public NullArgumentException(int argumentIndex, String argumentType) {
		super("param " + argumentIndex + " (type " + argumentType + ") is null");
	}

}
