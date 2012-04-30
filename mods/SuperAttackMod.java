import org.darkstorm.minecraft.darkmod.events.PacketEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.hooks.client.packets.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.methods.Location;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.tools.events.*;

public class SuperAttackMod extends Mod implements EventListener {
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
				Location entityLocation = new Location(entity.getX(),
						entity.getY(), entity.getZ());
				if(getDistanceBetween(playerLocation, entityLocation) > 3)
					break;
				Packet18Animation armSwing = (Packet18Animation) ReflectionUtil
						.instantiate(ClassRepository
								.getClassForInterface(Packet18Animation.class));
				Packet7UseEntity attackPacket = (Packet7UseEntity) ReflectionUtil
						.instantiate(ClassRepository
								.getClassForInterface(Packet7UseEntity.class));
				attackPacket.setPlayerEntityID(playerID);
				attackPacket.setEntityID(id);
				attackPacket.setButton(1);
				networkHandler.sendPacket(armSwing);
				networkHandler.sendPacket(attackPacket);
				break;
			}
			if(found)
				return 250;
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return 400;
	}

	@Override
	public void onEvent(Event event) {
		PacketEvent packetEvent = (PacketEvent) event;
		if(packetEvent.getStatus() != PacketEvent.SENT)
			return;
		Player player = minecraft.getPlayer();
		Packet packet = packetEvent.getPacket();
		if(!(packet instanceof Packet7UseEntity) || player == null)
			return;
		Packet7UseEntity packetInteract = (Packet7UseEntity) packet;
		if(packetInteract.getButton() == 1
				&& packetInteract.getPlayerEntityID() == player.getID()
				&& packetInteract.getEntityID() != player.getID())
			targetID = packetInteract.getEntityID();
	}
}
