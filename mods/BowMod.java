import java.util.*;

import org.darkstorm.minecraft.darkmod.events.*;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.hooks.client.packets.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.tools.events.Event;
import org.lwjgl.opengl.GL11;

public class BowMod extends Mod implements CommandListener {
	private AutoAttackMod attackMod;
	private int tickTimer = 0;
	private boolean target = false;
	private Queue<Entity> attackQueue = new ArrayDeque<Entity>();
	private Map<Entity, EntityTracker> trackers = new HashMap<Entity, EntityTracker>();

	public BowMod() {
	}

	@Override
	public void onStart() {
		attackMod = (AutoAttackMod) handler.getModByName("Auto Attack Mod");
		commandManager.registerListener(new Command("target", "/target",
				"Auto-target toggle"), this);
		eventManager.addListener(TickEvent.class, this);
		eventManager.addListener(PacketEvent.class, this);
		eventManager.addListener(RenderEvent.class, this);
	}

	@Override
	public void onStop() {
		eventManager.removeListener(TickEvent.class, this);
		eventManager.removeListener(PacketEvent.class, this);
		eventManager.removeListener(RenderEvent.class, this);
	}

	@Override
	public int loop() throws InterruptedException {
		return 90000;
	}

	@Override
	public String getName() {
		return "Bow Mod";
	}

	@Override
	public String getShortDescription() {
		return "Aim, fast-fire, etc.";
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
	public synchronized void onEvent(Event event) {
		if(event instanceof PacketEvent) {
			if(target)
				return;
			PacketEvent packetEvent = (PacketEvent) event;
			if(!(packetEvent.getPacket() instanceof Packet15BlockPlace))
				return;
			Packet15BlockPlace placePacket = (Packet15BlockPlace) packetEvent
					.getPacket();
			if(placePacket.getX() != -1 || placePacket.getY() != -1
					|| placePacket.getZ() != -1
					|| placePacket.getDirection() != 255)
				return;
			NetworkHandler networkHandler = ((MultiplayerWorld) minecraft
					.getWorld()).getNetworkHandler();
			Packet12PlayerRotate rotatePacket = (Packet12PlayerRotate) ReflectionUtil
					.instantiate(ClassRepository
							.getClassForInterface(Packet12PlayerRotate.class));
			Player player = minecraft.getPlayer();
			rotatePacket.setRotationX(player.getRotationX());
			rotatePacket.setRotationY(player.getRotationY());
			rotatePacket.setOnGround(player.isOnGround());
			for(int i = 0; i < 30; i++)
				networkHandler.sendPacket(rotatePacket);
			Packet14BlockDig digPacket = (Packet14BlockDig) ReflectionUtil
					.instantiate(ClassRepository
							.getClassForInterface(Packet14BlockDig.class));
			digPacket.setX(0);
			digPacket.setY(0);
			digPacket.setZ(0);
			digPacket.setFace(255);
			digPacket.setStatus(5);
			networkHandler.sendPacket(digPacket);
		} else if(event instanceof RenderEvent
				&& ((RenderEvent) event).getStatus() == RenderEvent.RENDER_ENTITIES_END) {
			try {
				Player player = minecraft.getPlayer();
				World world = minecraft.getWorld();
				if(player == null || world == null)
					return;
				double playerX = player.getX();
				double playerY = player.getY();
				double playerZ = player.getZ();
				double horizontalSize = 0.45;
				double verticalSize = 0.35;
				for(EntityTracker tracker : trackers.values()) {
					if(tracker.index == 0
							|| !(tracker.entity instanceof Humanoid))
						continue;
					double lastX = playerX - tracker.lastX[0];
					double lastY = playerY - tracker.lastY[0];
					double lastZ = playerZ - tracker.lastZ[0];
					for(int i = 1; i < tracker.index; i++) {
						double x = (playerX - tracker.lastX[i]);
						double y = (playerY - tracker.lastY[i]);
						double z = (playerZ - tracker.lastZ[i]);
						{
							GL11.glPushMatrix();
							setColor(getColor(((Humanoid) tracker.entity)
									.getName()));
							GL11.glLineWidth(2);
							GL11.glDisable(GL11.GL_TEXTURE_2D);
							GL11.glDisable(GL11.GL_LIGHTING);
							GL11.glBlendFunc(GL11.GL_SRC_ALPHA,
									GL11.GL_ONE_MINUS_SRC_ALPHA);
							GL11.glEnable(GL11.GL_LINE_SMOOTH);
							GL11.glBegin(GL11.GL_LINE_LOOP);
							GL11.glVertex3d((-lastX + horizontalSize) - 0.5,
									(verticalSize - lastY) + 1.0,
									(-lastZ - horizontalSize) + 0.5);
							GL11.glVertex3d((-x + horizontalSize) - 0.5,
									(verticalSize - y) + 1.0,
									(-z - horizontalSize) + 0.5);
							GL11.glEnd();
							GL11.glDisable(GL11.GL_LINE_SMOOTH);
							GL11.glEnable(GL11.GL_LIGHTING);
							GL11.glEnable(GL11.GL_TEXTURE_2D);
							GL11.glPopMatrix();
						}
						lastX = x;
						lastY = y;
						lastZ = z;
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else if(!(event instanceof TickEvent))
			return;
		tickTimer++;
		Player player = minecraft.getPlayer();
		World world = minecraft.getWorld();
		if(player == null || world == null)
			return;
		List<Entity> entitiesNotUpdated = new ArrayList<Entity>(
				trackers.keySet());
		List<Entity> entities = new ArrayList<Entity>(world.getEntities());
		for(Entity entity : entities) {
			if(entity == null || entity.getID() == player.getID())
				continue;
			EntityTracker tracker = trackers.get(entity);
			if(tracker == null) {
				tracker = new EntityTracker(entity);
				trackers.put(entity, tracker);
				continue;
			}
			entitiesNotUpdated.remove(entity);
			tracker.update();
		}
		for(Entity entity : entitiesNotUpdated)
			trackers.remove(entity);
		if(tickTimer < 6)
			return;
		tickTimer = 0;
		if(!target)
			return;

		NetworkHandler networkHandler = ((MultiplayerWorld) minecraft
				.getWorld()).getNetworkHandler();
		if(attackQueue.isEmpty())
			attackQueue.addAll(world.getEntities());
		Entity entity = null;
		while(attackQueue.peek() != null) {
			entity = attackQueue.poll();
			if(entity.getID() == player.getID()
					|| !(entity instanceof Animable)
					|| getDistanceBetween(player, entity) > 10) {
				entity = null;
				continue;
			}
			if(entity instanceof Humanoid
					&& (attackMod.isFriend(((Humanoid) entity).getName()) || (attackMod
							.getTeam() != null && attackMod.getTeam().isOnTeam(
							((Humanoid) entity))))) {
				entity = null;
				continue;
			}
			break;
		}
		if(entity == null)
			return;
		EntityTracker tracker = trackers.get(entity);
		if(tracker == null)
			return;

		float originalX = player.getRotationX();
		float originalY = player.getRotationY();
		float rotationX = getRotationXForShooting(tracker);
		float rotationY = getRotationYForShooting(tracker);

		Packet15BlockPlace placePacket = (Packet15BlockPlace) ReflectionUtil
				.instantiate(ClassRepository
						.getClassForInterface(Packet15BlockPlace.class));
		placePacket.setX(-1);
		placePacket.setY(-1);
		placePacket.setZ(-1);
		placePacket.setDirection(255);
		placePacket.setItem(player.getInventory().getSelectedItem());
		networkHandler.sendPacket(placePacket);

		Packet12PlayerRotate rotatePacket = (Packet12PlayerRotate) ReflectionUtil
				.instantiate(ClassRepository
						.getClassForInterface(Packet12PlayerRotate.class));
		rotatePacket.setRotationX(rotationX);
		rotatePacket.setRotationY(rotationY);
		rotatePacket.setOnGround(player.isOnGround());

		for(int i = 0; i < 30; i++)
			networkHandler.sendPacket(rotatePacket);

		Packet14BlockDig digPacket = (Packet14BlockDig) ReflectionUtil
				.instantiate(ClassRepository
						.getClassForInterface(Packet14BlockDig.class));
		digPacket.setX(0);
		digPacket.setY(0);
		digPacket.setZ(0);
		digPacket.setFace(255);
		digPacket.setStatus(5);
		networkHandler.sendPacket(digPacket);

		Packet12PlayerRotate rotatePacket2 = (Packet12PlayerRotate) ReflectionUtil
				.instantiate(ClassRepository
						.getClassForInterface(Packet12PlayerRotate.class));
		rotatePacket2.setRotationX(originalX);
		rotatePacket2.setRotationY(originalY);
		rotatePacket2.setOnGround(player.isOnGround());
		networkHandler.sendPacket(rotatePacket2);
	}

	private void setColor(int color) {
		float r = (color >> 16 & 0xff) / 255F;
		float g = (color >> 8 & 0xff) / 255F;
		float b = (color & 0xff) / 255F;

		GL11.glColor4f(r, g, b, 1f);
	}

	private int getColor(String player) {
		return (player.hashCode() & 0xaaaaaa) + 0x444444;
	}

	public float getRotationXForShooting(EntityTracker tracker) {
		Player player = minecraft.getPlayer();
		double d = tracker.predictedX - player.getX();
		double d1 = tracker.predictedZ - player.getZ();
		return (float) ((Math.atan2(d1, d) * 180D) / 3.1415927410125732D) - 90F;
	}

	public float getRotationYForShooting(EntityTracker tracker) {
		Player player = minecraft.getPlayer();
		double dis1 = tracker.predictedY + 1 - player.getY() + 1;
		double dis2 = Math.sqrt(Math.pow(tracker.predictedX - player.getX(), 2)
				+ Math.pow(tracker.predictedZ - player.getZ(), 2));
		return (float) ((Math.atan2(dis2, dis1) * 180D) / 3.1415927410125732D)
				- 80F
				- ((float) Math.pow(
						getDistanceTo(player, tracker.predictedX,
								tracker.predictedY, tracker.predictedZ) / 4, 2));
	}

	public float getDifference(float f1, float f2) {
		if(f1 == f2)
			return 0;
		if(f1 < f2)
			return Math.abs(f2 - f1);
		return Math.abs(f1 - f2);
	}

	@Override
	public void onCommand(String command) {
		if(command.equalsIgnoreCase("target")) {
			target = !target;
			displayText(ChatColor.GRAY + "Auto-targetting is now "
					+ ChatColor.GOLD + (target ? "on" : "off"));
		}
	}

	private class EntityTracker {
		private Entity entity;
		private double x, y, z;
		private double[] lastX = new double[30], lastY = new double[30],
				lastZ = new double[30];
		private double predictedX, predictedY, predictedZ;
		private int index = 0;

		public EntityTracker(Entity entity) {
			this.entity = entity;
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
			update();
		}

		public void update() {
			if(index < lastX.length)
				index++;
			for(int i = lastX.length - 1; i > 0; i--) {
				lastX[i] = lastX[i - 1];
				lastY[i] = lastY[i - 1];
				lastZ[i] = lastZ[i - 1];
			}
			lastX[0] = x;
			lastY[0] = y;
			lastZ[0] = z;
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
			predictedX = x;// (x + (x - lastX)) * 2;
			predictedY = y;// (y + (y - lastY)) * 2;
			predictedZ = z;// (z + (z - lastZ)) * 2;
		}
	}
}
