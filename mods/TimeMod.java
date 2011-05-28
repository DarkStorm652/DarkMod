import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.tools.strings.StringTools;

public class TimeMod extends Mod implements CommandListener {
	@Override
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public String getName() {
		return "Time Mod";
	}

	@Override
	public String getShortDescription() {
		return "Adds /time to control time";
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public void onStart() {
		commandManager.registerListener(new Command("time",
				"/time [day|night|(0-24000)]", "Time control options"), this);
	}

	@Override
	public void onStop() {
		commandManager.unregisterListener("time");
	}

	@Override
	public int loop() throws InterruptedException {
		return 1000;
	}

	@Override
	public void onCommand(String command) {
		String[] parts = command.split(" ");
		if((parts[0].equalsIgnoreCase("time"))) {
			World world = minecraft.getWorld();
			if(world == null)
				return;
			WorldInfo worldInfo = world.getWorldInfo();
			if(parts.length == 1) {
				displayText(ChatColor.GRAY + "Time is " + worldInfo.getTime()
						+ ".");
			} else if(parts[1].equalsIgnoreCase("day")) {
				worldInfo.setTime(0);
				displayText(ChatColor.GRAY + "It is now daytime.");
			} else if(parts[1].equalsIgnoreCase("night")) {
				worldInfo.setTime(14000);
				displayText(ChatColor.GRAY + "It is now nighttime.");
			} else if(StringTools.isLong(parts[1])) {
				worldInfo.setTime(Long.parseLong(parts[1]));
				displayText(ChatColor.GRAY + "It is now " + worldInfo.getTime()
						+ ".");
			}
		}
	}

}
