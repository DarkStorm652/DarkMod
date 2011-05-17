import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.methods.Location;
import org.darkstorm.minecraft.darkmod.tools.*;

public class AutoAttackMod extends Mod {
	private Class<?> attackPacketClass;

	@Override
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public String getName() {
		return "Auto Attack Mod";
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
	public void onStart() {
		if(attackPacketClass == null)
			attackPacketClass = ClassRepository.getClassByName("a");
	}

	@Override
	public int loop() throws InterruptedException {
		try {
			if(minecraft.getWorld() == null
					|| !(minecraft.getWorld() instanceof MultiplayerWorld))
				return 5000;
			Player player = minecraft.getPlayer();
			int playerID = player.getID();
			Location playerLocation = new Location(player.getX(), player.getY(),
					player.getZ());
			MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
			NetworkHandler networkHandler = world.getNetworkHandler();
			for(Entity entity : world.getEntities()) {
				if(entity.equals(player) || !(entity instanceof Animable))
					continue;
				Location entityLocation = new Location(entity.getX(),
						entity.getY(), entity.getZ());
				if(getDistanceBetween(playerLocation, entityLocation) > 5)
					continue;
				int id = entity.getID();
				Packet attackPacket = (Packet) ReflectionUtil.instantiate(
						attackPacketClass, new Class[] { Integer.TYPE,
								Integer.TYPE, Integer.TYPE }, playerID, id, 1);
				networkHandler.sendPacket(attackPacket);
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return 500;
	}
}
