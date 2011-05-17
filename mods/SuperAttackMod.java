import java.lang.reflect.Field;

import org.darkstorm.minecraft.darkmod.events.PacketEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.methods.Location;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.tools.events.*;

public class SuperAttackMod extends Mod implements EventListener {
	private Class<?> attackPacketClass;
	private int targetID = -1;

	@Override
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public String getName() {
		return "Super Attack Mod";
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
		eventManager.addListener(PacketEvent.class, this);
	}

	@Override
	public void onStop() {
		eventManager.removeListener(PacketEvent.class, this);
	}

	@Override
	public int loop() throws InterruptedException {
		try {
			if(minecraft.getWorld() == null
					|| !(minecraft.getWorld() instanceof MultiplayerWorld))
				return 5000;
			Player player = minecraft.getPlayer();
			int playerID = player.getID();
			Location playerLocation = new Location(player.getX(),
					player.getY(), player.getZ());
			MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
			NetworkHandler networkHandler = world.getNetworkHandler();
			boolean found = false;
			for(Entity entity : world.getEntities()) {
				int id = entity.getID();
				if(entity.equals(player) || !(entity instanceof Animable)
						|| id != targetID)
					continue;
				found = true;
				Location entityLocation = new Location(entity.getX(), entity
						.getY(), entity.getZ());
				if(getDistanceBetween(playerLocation, entityLocation) > 2)
					break;
				Packet attackPacket = (Packet) ReflectionUtil.instantiate(
						attackPacketClass, new Class[] { Integer.TYPE,
								Integer.TYPE, Integer.TYPE }, playerID, id, 1);
				networkHandler.sendPacket(attackPacket);
				break;
			}
			if(found)
				return 250;
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return 500;
	}

	@Override
	public void onEvent(Event event) {
		PacketEvent packetEvent = (PacketEvent) event;
		Packet packet = packetEvent.getPacket();
		if(!attackPacketClass.isInstance(packet))
			return;
		try {
			Field entityIDField = attackPacketClass.getDeclaredField("b");
			targetID = entityIDField.getInt(packet);
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
}
