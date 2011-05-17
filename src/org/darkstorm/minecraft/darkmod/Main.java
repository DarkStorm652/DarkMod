package org.darkstorm.minecraft.darkmod;

import java.util.Arrays;

import joptsimple.*;

import org.darkstorm.minecraft.darkmod.tools.Tools;

public class Main {
	public static void main(String[] args) {
		OptionParser parser = new OptionParser();
		OptionSpec<?> help = parser.acceptsAll(Arrays.asList("h", "help"),
				"Show help");
		OptionSpec<?> version = parser.accepts("version", "Show version");
		OptionSpec<String> username = parser.acceptsAll(
				Arrays.asList("u", "username"), "Use a username")
				.withRequiredArg().ofType(String.class).describedAs("username");

		OptionSpec<String> password = parser.acceptsAll(
				Arrays.asList("p", "password"),
				"Use a password (requires username)").withRequiredArg().ofType(
				String.class).describedAs("password");

		OptionSpec<String> sessionID = parser.acceptsAll(
				Arrays.asList("s", "session-id"),
				"Use a session ID (requires username and password)")
				.withRequiredArg().ofType(String.class).describedAs(
						"session-id");
		OptionSet options;
		try {
			options = parser.parse(args);
		} catch(OptionException exception) {
			try {
				parser.printHelpOn(System.out);
			} catch(Exception exception1) {
				exception1.printStackTrace();
			}
			return;
		}
		if(options.has(help)) {
			try {
				parser.printHelpOn(System.out);
			} catch(Exception exception) {
				exception.printStackTrace();
			}
			return;
		} else if(options.has(version)) {
			System.out.println("DarkMod " + DarkMod.getVersion());
			if(Tools.getMinecraftVersion() != -1)
				System.out.print("Minecraft build "
						+ Tools.getMinecraftVersion());
			return;
		}
		OptionSpec<?>[] optionSpecs = { username, password, sessionID };
		for(OptionSpec<?> option : optionSpecs) {
			if(options.has(option) && !options.hasArgument(option)) {
				System.out.println("No argument given for option " + option
						+ "\n\nUsage:");
				try {
					parser.printHelpOn(System.out);
				} catch(Exception exception) {
					exception.printStackTrace();
				}
				return;
			}
		}
		if((options.has(sessionID) && (!options.has(username) || !options
				.has("password")))) {
			System.out
					.println("Session ID option requires a username and password"
							+ "\n\nUsage:");
			try {
				parser.printHelpOn(System.out);
			} catch(Exception exception) {
				exception.printStackTrace();
			}
			return;
		}
		if(options.has(password) && !options.has(username)) {
			System.out.println("Password option requires a username"
					+ "\n\nUsage:");
			try {
				parser.printHelpOn(System.out);
			} catch(Exception exception) {
				exception.printStackTrace();
			}
			return;
		}
		if(options.has(username) && options.has(password)
				&& options.has(sessionID))
			new DarkMod(options.valueOf(username), options.valueOf(password),
					options.valueOf(sessionID));
		else if(options.has(username) && options.has(password))
			new DarkMod(options.valueOf(username), options.valueOf(password));
		else if(options.has(username))
			new DarkMod(options.valueOf(username));
		else
			new DarkMod();
	}
}
