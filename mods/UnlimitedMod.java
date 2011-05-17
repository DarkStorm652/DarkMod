import org.darkstorm.minecraft.darkmod.hooks.client.MultiplayerPlayer;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;

public class UnlimitedMod extends Mod implements CommandListener {

	@Override
	public ModControl getControlOption() {
		return ModControl.NONE;
	}

	@Override
	public String getName() {
		return "Unlimited Mod";
	}

	@Override
	public String getShortDescription() {
		return "Adds /u, /ugive, and /ug shortcuts";
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public int loop() throws InterruptedException {
		return 1000;
	}

	@Override
	public void onStart() {
		commandManager.registerListener(new Command("u", "/u <item|list>",
				"Shortcut for /unlimited"), this);
		commandManager.registerListener(new Command("ug", "/ug <item>",
				"Sends two /unlimited commands to spawn an item"), this);
		commandManager.registerListener(new Command("ugive", "/ugive <item>",
				"Sends two /unlimited commands to spawn an item"), this);
	}

	@Override
	public void onStop() {
		commandManager.unregisterListener("u");
		commandManager.unregisterListener("ug");
		commandManager.unregisterListener("ugive");
	}

	@Override
	public void onCommand(String command) {
		String[] parts = command.split(" ");
		if(parts.length != 2
				|| !(minecraft.getPlayer() instanceof MultiplayerPlayer))
			return;
		MultiplayerPlayer player = (MultiplayerPlayer) minecraft.getPlayer();
		command = command.toLowerCase();
		if(command.startsWith("u"))
			player.sendText("/unlimited " + parts[1]);
		if(parts[0].equalsIgnoreCase("ug")
				|| parts[0].equalsIgnoreCase("ugive")
				&& !parts[1].equalsIgnoreCase("list"))
			player.sendText("/unlimited " + parts[1]);
	}

}
