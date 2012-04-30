import org.darkstorm.minecraft.darkmod.events.TickEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.hooks.client.packets.Packet12PlayerRotate;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.tools.events.Event;

public class SuperHealthMod extends Mod {

	public SuperHealthMod() {
	}

	@Override
	public void onStart() {
		eventManager.addListener(TickEvent.class, this);
	}

	@Override
	public void onStop() {
		eventManager.removeListener(TickEvent.class, this);
	}

	@Override
	public int loop() throws InterruptedException {
		return 90000;
	}

	@Override
	public String getName() {
		return "Super Health Mod";
	}

	@Override
	public String getShortDescription() {
		return "Send a large amount of movement updates.";
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
		Player player = minecraft.getPlayer();
		World world = minecraft.getWorld();
		if(player == null || world == null
				|| !(world instanceof MultiplayerWorld))
			return;
		NetworkHandler networkHandler = ((MultiplayerWorld) minecraft
				.getWorld()).getNetworkHandler();
		Packet12PlayerRotate rotatePacket = (Packet12PlayerRotate) ReflectionUtil
				.instantiate(ClassRepository
						.getClassForInterface(Packet12PlayerRotate.class));
		rotatePacket.setRotationX(player.getRotationX());
		rotatePacket.setRotationY(player.getRotationY());
		rotatePacket.setOnGround(player.isOnGround());

		for(int i = 0; i < 30; i++)
			networkHandler.sendPacket(rotatePacket);
	}
}
