import java.util.*;

import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.tools.strings.StringTools;

public class HelpMod extends Mod implements CommandListener {
	private int commandsPerPage = 7;
	private int maxCharactersPerLine = 60;

	@Override
	public ModControl getControlOption() {
		return ModControl.NONE;
	}

	@Override
	public String getName() {
		return "Help Command Mod";
	}

	@Override
	public String getShortDescription() {
		return "Adds /help";
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public void onStart() {
		commandManager.registerListener(new Command("help", "/help [command]",
				"Shows command help"), this);
	}

	@Override
	public void onStop() {
		commandManager.unregisterListeners(this);
	}

	@Override
	public int loop() throws InterruptedException {
		return 1000;
	}

	@Override
	public void onCommand(String command) {
		String[] parts = command.split(" ");
		if(parts.length == 1) {
			Command[] commands = commandManager.getRegisteredCommands();
			sortCommands(commands);
			int pages = (commands.length / commandsPerPage)
					+ (commands.length % commandsPerPage > 0 ? 1 : 0);
			displayText(ChatColor.GOLD + "____________.[ " + ChatColor.GRAY
					+ "Help (1/" + pages + ")" + ChatColor.GOLD
					+ " ].___________");
			for(int i = 0; i < commandsPerPage; i++) {
				if(i + 1 > commands.length)
					break;
				Command cmd = commands[i];
				String name = cmd.getCommand();
				String usage = cmd.getUsage();
				String message = ChatColor.GOLD + name + ": " + ChatColor.GRAY
						+ usage;
				while(ChatColor.removeColors(message).length() > maxCharactersPerLine) {
					displayText(ChatColor.GRAY
							+ message.substring(0, maxCharactersPerLine));
					message = message.substring(maxCharactersPerLine);
				}
				if(ChatColor.removeColors(message).length() > 0)
					displayText(ChatColor.GRAY + message);
			}
		} else if(parts.length == 2) {
			if(StringTools.isInteger(parts[1])) {
				int page = Integer.parseInt(parts[1]);
				Command[] commands = commandManager.getRegisteredCommands();
				sortCommands(commands);
				int pages = (commands.length / commandsPerPage)
						+ (commands.length % commandsPerPage > 0 ? 1 : 0);
				if(page < 1 || page > pages) {
					displayText(ChatColor.GRAY + "Page not found.");
					return;
				}
				displayText(ChatColor.GOLD + "____________.[ " + ChatColor.GRAY
						+ "Help (" + page + "/" + pages + ")" + ChatColor.GOLD
						+ " ].___________");
				for(int i = 0; i < commandsPerPage; i++) {
					if(i + ((page - 1) * commandsPerPage) + 1 > commands.length)
						break;
					Command cmd = commands[i + ((page - 1) * commandsPerPage)];
					String name = cmd.getCommand();
					String usage = cmd.getUsage();
					String message = ChatColor.GOLD + name + ": "
							+ ChatColor.GRAY + usage;
					while(ChatColor.removeColors(message).length() > maxCharactersPerLine) {
						displayText(ChatColor.GRAY
								+ message.substring(0, maxCharactersPerLine));
						message = message.substring(maxCharactersPerLine);
					}
					if(ChatColor.removeColors(message).length() > 0)
						displayText(ChatColor.GRAY + message);
				}
			} else {
				String commandName = parts[1];
				for(Command cmd : commandManager.getRegisteredCommands()) {
					if(cmd.matches(commandName)) {
						String message = ChatColor.GOLD + cmd.getUsage() + ": "
								+ ChatColor.GRAY + cmd.getDescription();
						while(ChatColor.removeColors(message).length() > maxCharactersPerLine) {
							displayText(ChatColor.GRAY
									+ message
											.substring(0, maxCharactersPerLine));
							message = message.substring(maxCharactersPerLine);
						}
						if(ChatColor.removeColors(message).length() > 0)
							displayText(ChatColor.GRAY + message);
					}
				}
			}
		}
	}

	private Command[] sortCommands(Command[] commands) {
		Arrays.sort(commands, new Comparator<Command>() {
			@Override
			public int compare(Command command1, Command command2) {
				String commandName1 = command1.getCommand();
				String commandName2 = command2.getCommand();
				return commandName1.compareTo(commandName2);
			}
		});
		return commands;
	}

}
