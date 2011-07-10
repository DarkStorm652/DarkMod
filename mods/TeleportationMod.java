import org.darkstorm.minecraft.darkmod.hooks.client.Player;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.tools.strings.StringTools;

public class TeleportationMod extends Mod implements CommandListener {
	@Override
	public void onStart() {
		commandManager.registerListener(new Command("teleport",
				"/teleport <<n|s|e|w|u|d> <distance>|<x> <y> <z>>",
				"Teleports you"), this);
	}

	@Override
	public void onStop() {
		commandManager.unregisterListener("teleport");
	}

	@Override
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public String getName() {
		return "Teleportation Mod";
	}

	@Override
	public String getShortDescription() {
		return "Allows you to teleport with /tp";
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public int loop() throws InterruptedException {
		return 500;
	}

	@Override
	public void onCommand(String command) {
		Player player = minecraft.getPlayer();
		if(player == null)
			return;
		String[] parts = command.split(" ");
		if(parts[0].equalsIgnoreCase("teleport") && parts.length > 1) {
			if(parts.length == 4 && StringTools.isInteger(parts[1])
					&& StringTools.isInteger(parts[2])
					&& StringTools.isInteger(parts[3])) {
				int x = Integer.parseInt(parts[1]), y = Integer
						.parseInt(parts[2]), z = Integer.parseInt(parts[3]);
				player.setPosition(x, y, z);
				displayText(ChatColor.GRAY + "Teleported to " + ChatColor.GOLD
						+ "(" + x + ", " + y + ", " + z + ")");
			} else if(!StringTools.isInteger(parts[1]) && parts.length == 3
					&& StringTools.isInteger(parts[2])) {
				int amount = Integer.parseInt(parts[2]);
				String direction;
				if(parts[1].startsWith("n")) {
					player.setX(player.getX() - amount);
					direction = "north";
				} else if(parts[1].startsWith("s")) {
					player.setX(player.getX() + amount);
					direction = "south";
				} else if(parts[1].startsWith("e")) {
					player.setZ(player.getZ() - amount);
					direction = "east";
				} else if(parts[1].startsWith("w")) {
					player.setZ(player.getZ() + amount);
					direction = "west";
				} else if(parts[1].startsWith("u")) {
					player.setY(player.getY() + amount);
					direction = "up";
				} else if(parts[1].startsWith("d")) {
					player.setY(player.getY() + amount);
					direction = "down";
				} else {
					displayText(ChatColor.GRAY + "Invalid direction");
					return;
				}
				displayText(ChatColor.GRAY + "Teleported " + ChatColor.GOLD
						+ amount + " " + direction);
			} else
				displayText(ChatColor.GRAY + "Invalid arguments");
		}
	}
}
