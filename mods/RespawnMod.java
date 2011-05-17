import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;

public class RespawnMod extends Mod implements CommandListener {

	@Override
	public ModControl getControlOption() {
		return ModControl.NONE;
	}

	@Override
	public String getName() {
		return "Respawn Mod";
	}

	@Override
	public String getShortDescription() {
		return "Adds /respawn command to the game";
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public int loop() throws InterruptedException {
		return -1;
	}

	@Override
	public void onStart() {
		commandManager.registerListener(new Command("respawn", "/respawn",
				"Causes you to respawn"), this);
	}

	@Override
	public void onStop() {
		commandManager.unregisterListener("respawn");
	}

	@Override
	public void onCommand(String command) {
		if(command.equalsIgnoreCase("respawn")
				&& minecraft.getWorld() instanceof MultiplayerWorld) {
			ClassLoader classLoader = accessHandler.getClassLoader();
			try {
				Class<?> respawnPacketClass = classLoader.loadClass("ln");
				Packet respawnPacket = (Packet) respawnPacketClass
						.newInstance();
				((MultiplayerWorld) minecraft.getWorld()).getNetworkHandler()
						.sendPacket(respawnPacket);
			} catch(Exception exception) {
				exception.printStackTrace();
			}
		}
	}

}
