import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;

public class SuperChickensMod extends Mod {
	@Override
	public String getName() {
		return "Super Chickens Mod";
	}

	@Override
	public String getShortDescription() {
		return "EGGS!!! What did you put in their fertilizer!?";
	}

	@Override
	public String getFullDescription() {
		return "<html>Makes chickens lay eggs at 1 per frame (causes laggg)"
				+ "</html>";
	}

	@Override
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public int loop() {
		World world = minecraft.getWorld();
		if(world != null)
			for(Entity entity : world.getEntities())
				if(entity instanceof Chicken)
					((Chicken) entity).setEggDropTimer(0);
		return 0;
	}

}
