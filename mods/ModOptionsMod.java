import org.darkstorm.minecraft.darkmod.mod.Mod;

public class ModOptionsMod extends Mod {

	@Override
	public int loop() throws InterruptedException {
		// GuiScreen screen = minecraft.getCurrentScreen();
		// if(screen.getClass())
		return 10;
	}

	@Override
	public String getName() {
		return "Mod Options";
	}

	@Override
	public String getShortDescription() {
		return "Adds options GUI";
	}

	@Override
	public ModControl getControlOption() {
		return ModControl.NONE;
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

}
