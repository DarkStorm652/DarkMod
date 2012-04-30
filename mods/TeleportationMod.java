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
		commandManager.registerListener(new Command("tp",
				"/tp <<n|s|e|w|u|d> <distance>|<x> <y> <z>>", "Teleports you"),
				this);
	}

	@Override
	public void onStop() {
		commandManager.unregisterListeners(this);
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
		if(parts.length > 1) {
			if(parts.length == 4 && StringTools.isDouble(parts[1])
					&& StringTools.isDouble(parts[2])
					&& StringTools.isDouble(parts[3])) {
				double x = Double.parseDouble(parts[1]), y = Double
						.parseDouble(parts[2]), z = Double
						.parseDouble(parts[3]);
				player.setPosition(x, y, z);
				displayText(ChatColor.GRAY + "Teleported to " + ChatColor.GOLD
						+ "(" + x + ", " + y + ", " + z + ")");
			} else if(!StringTools.isDouble(parts[1]) && parts.length == 3
					&& StringTools.isDouble(parts[2])) {
				double amount = Double.parseDouble(parts[2]);
				String direction;
				if(parts[1].startsWith("n")) {
					player.setPositionAndAngles(player.getX() - amount,
							player.getY() - 1, player.getZ(),
							player.getRotationX(), player.getRotationY());
					direction = "north";
				} else if(parts[1].startsWith("s")) {
					player.setPositionAndAngles(player.getX() + amount,
							player.getY() - 1, player.getZ(),
							player.getRotationX(), player.getRotationY());
					direction = "south";
				} else if(parts[1].startsWith("e")) {
					player.setPositionAndAngles(player.getX(),
							player.getY() - 1, player.getZ() - amount,
							player.getRotationX(), player.getRotationY());
					direction = "east";
				} else if(parts[1].startsWith("w")) {
					player.setPositionAndAngles(player.getX(),
							player.getY() - 1, player.getZ() + amount,
							player.getRotationX(), player.getRotationY());
					direction = "west";
				} else if(parts[1].startsWith("u")) {
					player.setPositionAndAngles(player.getX(), player.getY()
							+ amount - 1, player.getZ(), player.getRotationX(),
							player.getRotationY());
					direction = "up";
				} else if(parts[1].startsWith("d")) {
					player.setPositionAndAngles(player.getX(), player.getY()
							- amount - 1, player.getZ(), player.getRotationX(),
							player.getRotationY());
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
