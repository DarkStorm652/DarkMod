import org.darkstorm.minecraft.darkmod.events.TickEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.methods.Location;
import org.darkstorm.tools.events.Event;

public class NoHeadshotMod extends Mod {
	private int ticksSinceLastTeleport = 0;
	private Location lastTeleportLocation = null;

	public NoHeadshotMod() {
	}

	@Override
	public int loop() throws InterruptedException {
		return 9000;
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
	public String getName() {
		return "No-Headshot Mod";
	}

	@Override
	public String getShortDescription() {
		return "Teleports you up 5 blocks when an arrow comes near";
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
		if(event instanceof TickEvent) {
			ticksSinceLastTeleport++;
			Player player = minecraft.getPlayer();
			World world = minecraft.getWorld();
			if(player != null && world != null) {
				if(ticksSinceLastTeleport < 10) {
					if(lastTeleportLocation != null)
						player.setPositionAndAngles(
								lastTeleportLocation.getX(),
								lastTeleportLocation.getY(),
								lastTeleportLocation.getZ(),
								player.getRotationX(), player.getRotationY());
					return;
				}
				Location playerLocation = new Location(player.getX(),
						player.getY(), player.getZ());
				for(Entity entity : world.getEntities()) {
					if(!(entity instanceof Arrow))
						continue;
					Arrow arrow = (Arrow) entity;
					Location arrowLocation = new Location(arrow.getX(),
							arrow.getY(), arrow.getZ());
					double distance = getDistanceBetween(playerLocation,
							arrowLocation);
					if(distance > 5 && distance < 12) {
						lastTeleportLocation = new Location(player.getX(),
								player.getY() + 2, player.getZ());
						player.setPositionAndAngles(
								lastTeleportLocation.getX(),
								lastTeleportLocation.getY(),
								lastTeleportLocation.getZ(),
								player.getRotationX(), player.getRotationY());
						ticksSinceLastTeleport = 0;
						break;
					}
				}
			}
		}
	}
}
