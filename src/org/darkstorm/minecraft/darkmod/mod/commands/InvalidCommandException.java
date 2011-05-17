package org.darkstorm.minecraft.darkmod.mod.commands;

public class InvalidCommandException extends RuntimeException {
	private static final long serialVersionUID = -171140730632051881L;

	public InvalidCommandException() {
	}

	public InvalidCommandException(String message) {
		super(message);
	}

	public InvalidCommandException(Throwable cause) {
		super(cause);
	}

	public InvalidCommandException(String message, Throwable cause) {
		super(message, cause);
	}
}
