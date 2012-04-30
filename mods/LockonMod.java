import java.util.*;

import org.darkstorm.minecraft.darkmod.events.*;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.hooks.client.packets.Packet7UseEntity;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.Location;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.tools.events.Event;

public class LockonMod extends Mod implements CommandListener {
	private String lockonName = null;
	private boolean tracking = true;
	private boolean sticking = true;

	public LockonMod() {
	}

	@Override
	public void onStart() {
		eventManager.addListener(TickEvent.class, this);
		eventManager.addListener(PacketEvent.class, this);
		commandManager.registerListener(new Command("lockon", "/lockon [name]",
				"Lock-on to a person"), this);
		commandManager.registerListener(new Command("lockmode",
				"/lockmode <stick|track>", "Lock-on Mod mode toggles"), this);
	}

	@Override
	public void onStop() {
		eventManager.removeListener(TickEvent.class, this);
		eventManager.removeListener(PacketEvent.class, this);
		commandManager.unregisterListeners(this);
	}

	@Override
	public int loop() throws InterruptedException {
		return 90000;
	}

	@Override
	public String getName() {
		return "Lock-on Mod";
	}

	@Override
	public String getShortDescription() {
		return "Teleport automatically to a person";
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
	public void onCommand(String command) {
		String[] parts = command.split(" ");
		if(parts[0].equalsIgnoreCase("lockon")) {
			if(parts.length == 2) {
				lockonName = parts[1];
				displayText(ChatColor.GRAY + "Locked onto " + ChatColor.GOLD
						+ lockonName + ".");
			} else {
				lockonName = null;
				displayText(ChatColor.GRAY + "Lock-on cleared.");
			}
		} else if(parts[0].equalsIgnoreCase("lockmode") && parts.length == 2) {
			if(parts[1].equalsIgnoreCase("stick")) {
				sticking = !sticking;
				displayText(ChatColor.GRAY
						+ "Lock-on mode "
						+ (sticking ? ChatColor.LIME + "enabled"
								: ChatColor.RED + "disabled") + ChatColor.GRAY
						+ ": " + ChatColor.GOLD + "stick");
			} else if(parts[1].equalsIgnoreCase("track")) {
				tracking = !tracking;
				displayText(ChatColor.GRAY
						+ "Lock-on mode "
						+ (sticking ? ChatColor.LIME + "enabled"
								: ChatColor.RED + "disabled") + ChatColor.GRAY
						+ ": " + ChatColor.GOLD + "track");
			}
		}
	}

	@Override
	public void onEvent(Event event) {
		if(event instanceof TickEvent && lockonName != null) {
			if(!sticking)
				return;
			World world = minecraft.getWorld();
			Player player = minecraft.getPlayer();
			String lockonName = this.lockonName;
			if(lockonName == null || player == null || world == null)
				return;
			for(Entity entity : world.getEntities()) {
				if(entity instanceof Humanoid
						&& lockonName.equalsIgnoreCase(ChatColor
								.removeColors(((Humanoid) entity).getName()))) {
					if(getDistanceBetween(
							new Location(player.getX(), player.getY(),
									player.getZ()), new Location(entity.getX(),
									entity.getY(), entity.getZ())) > 8)
						return;
					player.setPositionAndAngles(entity.getX(), entity.getY(),
							entity.getZ(), player.getRotationX(),
							player.getRotationY());
					player.setSpeedX(entity.getSpeedX());
					player.setSpeedY(entity.getSpeedY());
					player.setSpeedZ(entity.getSpeedZ());
					break;
				}
			}
		} else if(event instanceof PacketEvent
				&& ((PacketEvent) event).getStatus() == PacketEvent.SENT
				&& minecraft.getPlayer() != null
				&& minecraft.getWorld() != null) {
			PacketEvent packetEvent = (PacketEvent) event;
			Packet packet = packetEvent.getPacket();
			if(!(packet instanceof Packet7UseEntity))
				return;
			Packet7UseEntity packetInteract = (Packet7UseEntity) packet;
			if(packetInteract.getButton() != 0)
				return;
			Player player = minecraft.getPlayer();
			if(player == null
					|| packetInteract.getPlayerEntityID() != player.getID())
				return;
			int entityID = packetInteract.getEntityID();
			List<Entity> entities = new ArrayList<Entity>(minecraft.getWorld()
					.getEntities());
			for(Entity entity : entities) {
				if(entity.getID() != entityID)
					continue;
				if(!(entity instanceof Humanoid))
					break;
				if(lockonName != null
						&& lockonName.equals(ChatColor
								.removeColors(((Humanoid) entity).getName())))
					lockonName = null;
				else {
					lockonName = ChatColor.removeColors(((Humanoid) entity)
							.getName());
					displayText(ChatColor.GRAY + "Locked onto "
							+ ChatColor.GOLD + lockonName + ".");
				}
				break;
			}
		}
	}

	public String getLockonName() {
		return lockonName;
	}

	public boolean isTracking() {
		return tracking;
	}

	public boolean isSticking() {
		return sticking;
	}
}
