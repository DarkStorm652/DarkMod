import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.CommandListener;
import org.darkstorm.tools.events.*;

public class WasteManagementMod extends Mod implements CommandListener,
		EventListener {

	@Override
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public String getName() {
		return "Waste Management Mod";
	}

	@Override
	public String getShortDescription() {
		return "Heals when health gets low if food is in inventory";
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onStop() {
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public int loop() throws InterruptedException {
		System.gc();
		return 30000;
	}

	@Override
	public void onCommand(String command) {
	}

	@Override
	public void onEvent(Event event) {
	}

}
