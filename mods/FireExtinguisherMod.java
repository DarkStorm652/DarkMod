import org.darkstorm.minecraft.darkmod.events.TickEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.hooks.client.packets.Packet14BlockDig;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.tools.events.Event;

public class FireExtinguisherMod extends Mod {

	public FireExtinguisherMod() {
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
		return 9000;
	}

	@Override
	public String getName() {
		return "Fire Extinguisher Mod";
	}

	@Override
	public String getShortDescription() {
		return "Extinguishes all fires around you";
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
		if(event instanceof TickEvent)
			onTick();
	}

	private void onTick() {
		Player player = minecraft.getPlayer();
		if(player == null || !(player instanceof MultiplayerPlayer))
			return;
		MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
		if(world == null)
			return;
		int playerX = (int) Math.round(player.getX());
		int playerY = (int) Math.round(player.getY());
		int playerZ = (int) Math.round(player.getZ());
		int radiusHorizontal = 4;
		int radiusVertical = 3;
		for(int relativeX = -radiusHorizontal; relativeX <= radiusHorizontal; relativeX++) {
			for(int relativeZ = -radiusHorizontal; relativeZ <= radiusHorizontal; relativeZ++) {
				for(int relativeY = -radiusVertical; relativeY <= radiusVertical; relativeY++) {
					int x = playerX + relativeX, y = playerY + relativeY, z = playerZ
							+ relativeZ;
					if(world.getBlockIDAt(x, y, z) == 51) {
						Packet14BlockDig packet = (Packet14BlockDig) ReflectionUtil
								.instantiate(ClassRepository
										.getClassForInterface(Packet14BlockDig.class));
						Direction direction = getAdjacentDirection(x, y, z);
						if(direction == null)
							continue;
						packet.setX(x + direction.getXOffset());
						packet.setY(y + direction.getYOffset());
						packet.setZ(z + direction.getZOffset());
						packet.setFace(direction.getFace());
						packet.setStatus(0);
						world.getNetworkHandler().sendPacket(packet);
					}
				}
			}
		}
	}

	public Direction getAdjacentDirection(int x, int y, int z) {
		World world = minecraft.getWorld();
		if(world == null)
			return null;
		if(world.getBlockIDAt(x, y - 1, z) != 0)
			return Direction.DOWN;
		else if(world.getBlockIDAt(x, y + 1, z) != 0)
			return Direction.UP;
		else if(world.getBlockIDAt(x + 1, y, z) != 0)
			return Direction.NORTH;
		else if(world.getBlockIDAt(x - 1, y, z) != 0)
			return Direction.SOUTH;
		else if(world.getBlockIDAt(x, y, z + 1) != 0)
			return Direction.EAST;
		else if(world.getBlockIDAt(x, y, z - 1) != 0)
			return Direction.WEST;
		return null;
	}

	private enum Direction {
		NORTH(1, 0, 0, 4),
		SOUTH(-1, 0, 0, 5),
		EAST(0, 0, 1, 2),
		WEST(0, 0, -1, 3),
		UP(0, 1, 0, 0),
		DOWN(0, -1, 0, 1);

		private final int xOffset, yOffset, zOffset, face;

		private Direction(int xOffset, int yOffset, int zOffset, int face) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			this.zOffset = zOffset;
			this.face = face;
		}

		public int getXOffset() {
			return xOffset;
		}

		public int getYOffset() {
			return yOffset;
		}

		public int getZOffset() {
			return zOffset;
		}

		public int getFace() {
			return face;
		}
	}
}
