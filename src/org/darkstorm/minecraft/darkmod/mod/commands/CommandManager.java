package org.darkstorm.minecraft.darkmod.mod.commands;

import java.util.*;

import org.darkstorm.minecraft.darkmod.mod.ModHandler;

public class CommandManager {
	private ModHandler modHandler;
	private Map<Command, CommandListener> commandListeners;
	private Object lock = new Object();

	public CommandManager(ModHandler modHandler) {
		this.modHandler = modHandler;
		commandListeners = new HashMap<Command, CommandListener>();
	}

	public String handleText(String text) {
		if(text == null)
			return null;
		if(!text.startsWith("/"))
			return text;
		boolean doubleSlash = false;
		if(text.startsWith("//")) {
			text = text.substring(1);
			doubleSlash = true;
		}
		synchronized(lock) {
			for(Command registeredCommand : commandListeners.keySet()) {
				try {
					if(registeredCommand.matches(text)) {
						if(doubleSlash)
							return text;
						CommandListener commandListener = commandListeners
								.get(registeredCommand);
						try {
							commandListener.onCommand(text.substring(1));
						} catch(Exception exception) {
							exception.printStackTrace();
							commandListeners.remove(registeredCommand);
						}
						return null;
					}
				} catch(Exception exception) {}
			}
		}
		if(doubleSlash)
			return "/" + text;
		return text;
	}

	public void registerListener(Command command,
			CommandListener commandListener) {
		synchronized(lock) {
			if(command == null || commandListener == null)
				throw new NullPointerException();
			for(Command registeredCommand : commandListeners.keySet())
				if(command.matches(registeredCommand))
					throw new CommandConflictException(command + " -> "
							+ registeredCommand);
			commandListeners.put(command, commandListener);
		}
	}

	public CommandListener unregisterListener(Command command) {
		return unregisterListener(command.getCommand());
	}

	public CommandListener unregisterListener(String command) {
		synchronized(lock) {
			if(command == null)
				throw new NullPointerException();
			for(Command registeredCommand : commandListeners.keySet())
				if(registeredCommand.matches(command))
					return commandListeners.remove(registeredCommand);
			return null;
		}
	}

	public void unregisterListeners(CommandListener listener) {
		synchronized(lock) {
			if(listener == null)
				throw new NullPointerException();
			Set<Command> keys = new HashSet<Command>(commandListeners.keySet());
			for(Command command : keys) {
				CommandListener registeredListener = commandListeners
						.get(command);
				if(registeredListener.equals(listener))
					commandListeners.remove(command);
			}
		}
	}

	public boolean hasListener(String command) {
		return getListener(command) != null;
	}

	public CommandListener getListener(String command) {
		synchronized(lock) {
			if(command == null)
				throw new NullPointerException();
			if(command.startsWith("/") || command.contains(" ")
					|| command.length() < 1)
				throw new InvalidCommandException(command);
			return commandListeners.get(command);
		}
	}

	public Command[] getRegisteredCommands() {
		Set<Command> registeredCommands = commandListeners.keySet();
		return registeredCommands
				.toArray(new Command[registeredCommands.size()]);
	}

	public ModHandler getModHandler() {
		return modHandler;
	}
}
