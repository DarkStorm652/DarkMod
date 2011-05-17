package org.darkstorm.minecraft.darkmod.mod.commands;

public class CommandConflictException extends RuntimeException {
	private static final long serialVersionUID = -8579939081727151741L;

	public CommandConflictException() {
	}

	public CommandConflictException(String message) {
		super(message);
	}

	public CommandConflictException(Throwable cause) {
		super(cause);
	}

	public CommandConflictException(String message, Throwable cause) {
		super(message, cause);
	}
}
