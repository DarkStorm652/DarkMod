import org.darkstorm.minecraft.darkmod.hooks.client.MultiplayerPlayer;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;

public class ColorsMod extends Mod implements CommandListener {
	private String colors = "0123456789abcdef";
	private char color = '0';

	@Override
	public String getFullDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Colors Mod";
	}

	@Override
	public String getShortDescription() {
		return "";
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public void onStart() {
		commandManager.registerListener(new Command("color",
				"/color <show|set>", "Use /color for help."), this);
		commandManager
				.registerListener(
						new Command("csay", "/csay <text>",
								"Says something in a color. This will get you kicked :D"),
						this);
	}

	@Override
	public void onStop() {
		commandManager.unregisterListener("color");
		commandManager.unregisterListener("csay");
	}

	@Override
	public int loop() {
		return 500;
	}

	@Override
	public void onCommand(String command) {
		String[] parts = command.split(" ");
		if(parts[0].equalsIgnoreCase("color") && parts.length == 2) {
			if(parts[1].equalsIgnoreCase("show")) {
				String colorsColorized = "Colors: ";
				for(char color : colors.toCharArray()) {
					colorsColorized += ChatColor.valueOf(color).toString()
							+ color;
				}
				displayText(colorsColorized);
			} else if(parts[1].length() == 1 && colors.contains(parts[1])) {
				color = parts[1].charAt(0);
				displayText("Color set to " + ChatColor.valueOf(color) + color);
			}
		} else if(parts[0].equalsIgnoreCase("color")) {
			String red = ChatColor.RED.toString();
			displayText(red + "/color <show|set>");
			displayText(red + "   /color show");
			displayText(red + "      Show the available colors.");
			displayText(red + "   /color set <color>");
			displayText(red + "      Sets the color to the specified color.");
			displayText(red + "      Use /color show for a list of colors.");
		} else if(parts[0].equalsIgnoreCase("csay") && parts.length > 1) {
			String say = ChatColor.valueOf(color)
					+ command.substring("csay ".length());
			((MultiplayerPlayer) minecraft.getPlayer()).sendText(say);
		}
	}

}
