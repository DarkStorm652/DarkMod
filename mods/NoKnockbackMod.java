import org.darkstorm.minecraft.darkmod.events.PacketEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.Player;
import org.darkstorm.minecraft.darkmod.hooks.client.packets.Packet28EntityVelocity;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.tools.events.Event;

public class NoKnockbackMod extends Mod {

	public NoKnockbackMod() {
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
		return 90000;
	}

	@Override
	public String getName() {
		return "No Knockback Mod";
	}

	@Override
	public String getShortDescription() {
		return "Stops all entity velocity packets";
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
	public void onEvent(Event event) {
		PacketEvent packetEvent = (PacketEvent) event;
		Player player = minecraft.getPlayer();
		if(player == null
				|| !(packetEvent.getPacket() instanceof Packet28EntityVelocity))
			return;
		Packet28EntityVelocity velocityPacket = (Packet28EntityVelocity) packetEvent
				.getPacket();
		if(velocityPacket.getEntityID() == player.getID()) {
			velocityPacket.setSpeedX(0);
			velocityPacket.setSpeedY(0);
			velocityPacket.setSpeedZ(0);
		}
	}
}
