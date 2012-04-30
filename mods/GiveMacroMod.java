import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.hooks.client.MultiplayerPlayer;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.tools.strings.StringTools;

public class GiveMacroMod extends Mod implements CommandListener {

	@Override
	public ModControl getControlOption() {
		return ModControl.NONE;
	}

	@Override
	public void onStart() {
		commandManager.registerListener(new Command("g", "/g <id> [amount]",
				"Macro for /give, no max amount"), this);
	}

	@Override
	public void onStop() {
		commandManager.unregisterListener("g");
	}

	@Override
	public String getFullDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Give Macro";
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
	public int loop() {
		return 500;
	}

	@Override
	public void onCommand(String command) {
		if(!(minecraft.getPlayer() instanceof MultiplayerPlayer))
			return;
		String[] parts = command.split(" ");
		if(parts[0].equals("g") && parts.length >= 2 && parts.length <= 3) {
			String item = parts[1];
			int amount = 1;
			if(parts.length == 3)
				if(StringTools.isInteger(parts[2]))
					amount = Integer.parseInt(parts[2]);
				else
					displayText(ChatColor.DARK_RED + "Invalid amount");
			int stackCount = amount / 64;
			int remainder = amount % 64;
			for(int i = 0; i < stackCount; i++)
				((MultiplayerPlayer) minecraft.getPlayer()).sendText("/give "
						+ DarkMod.getInstance().getUsername() + " " + item
						+ " 64");
			if(remainder > 0)
				((MultiplayerPlayer) minecraft.getPlayer()).sendText("/give "
						+ DarkMod.getInstance().getUsername() + " " + item
						+ " " + remainder);
		}
	}
}
