import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.lwjgl.input.Mouse;

public class SignEditMod extends Mod {
	private boolean lastPressed;

	public SignEditMod() {
	}

	@Override
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public String getName() {
		return "Sign Edit Mod";
	}

	@Override
	public String getShortDescription() {
		return "Shows sign editing interface when right-clicking a sign";
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public int loop() throws InterruptedException {
		if(minecraft.getWorld() == null || minecraft.getCurrentScreen() != null)
			return 500;
		if(!Mouse.isButtonDown(1)) {
			if(lastPressed)
				lastPressed = false;
			return 10;
		} else if(Mouse.isButtonDown(1) && !lastPressed)
			lastPressed = true;
		else
			return 10;
		EntityTarget entityTarget = minecraft.getPlayerTarget();
		if(entityTarget == null)
			return 10;
		World world = minecraft.getWorld();
		int targetBlock = world.getBlockIDAt(entityTarget.getTargetX(),
				entityTarget.getTargetY(), entityTarget.getTargetZ());
		if(targetBlock != 63 && targetBlock != 68)
			return 10;
		Sign sign = (Sign) world.getTileEntityAt(entityTarget.getTargetX(),
				entityTarget.getTargetY(), entityTarget.getTargetZ());
		minecraft.displayScreen((GuiEditSign) ReflectionUtil.instantiate(
				ClassRepository.getClassForInterface(GuiEditSign.class),
				new Class<?>[] { ClassRepository
						.getClassForInterface(Sign.class) }, sign));
		return 10;
	}

}
