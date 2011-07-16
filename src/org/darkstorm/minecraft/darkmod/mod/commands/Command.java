package org.darkstorm.minecraft.darkmod.mod.commands;

public class Command {
	private String command, usage, description;

	public Command(String command, String usage, String description) {
		validateCommand(command);
		this.command = command;
		this.usage = usage;
		this.description = description;
	}

	private void validateCommand(String command) {
		if(command.length() < 1)
			throw new InvalidCommandException(command);
		for(char character : command.toCharArray())
			if(!Character.isLetter(character) && !Character.isDigit(character)
					&& character != '_')
				throw new InvalidCommandException(command);
	}

	public boolean matches(Command command) {
		return matches(command.getCommand());
	}

	public boolean matches(String command) {
		if(command.startsWith("/"))
			command = command.substring(1);
		if(command.contains(" ")) {
			String commandStart = command.split(" ")[0];
			validateCommand(commandStart);
			return commandStart.equalsIgnoreCase(this.command);
		} else {
			validateCommand(command);
			if(command.equalsIgnoreCase(this.command))
				return true;
		}
		return false;
	}

	public String getCommand() {
		return command;
	}

	public String getUsage() {
		return usage;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return command;
	}

}
