import org.darkstorm.minecraft.darkmod.events.TickEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.tools.events.Event;

public class AimMod extends Mod {
	private AutoAttackMod attackMod;
	private int tickTimer = 0;

	public AimMod() {
	}

	@Override
	public void onStart() {
		attackMod = (AutoAttackMod) handler.getModByName("Auto Attack Mod");
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
		return "Aim Mod";
	}

	@Override
	public String getShortDescription() {
		return "Aim at people who are closeby. Use bow mod for far away";
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
		tickTimer++;
		if(tickTimer < 2)
			return;
		tickTimer = 0;
		Player player = minecraft.getPlayer();
		if(player == null)
			return;
		Entity entity = getClosest(30);
		if(entity == null)
			return;
		if(entity instanceof Humanoid) {
			Humanoid otherPlayer = (Humanoid) entity;
			System.out.println("Aiming at "
					+ ChatColor.removeColors(otherPlayer.getName()));
		}
		player.setRotationX(getRotationXForShooting(entity));
		player.setRotationY(getRotationYForShooting(entity));
	}

	private Entity getClosest(double maxDistance) {
		Player player = minecraft.getPlayer();
		World world = minecraft.getWorld();
		if(world == null || player == null)
			return null;
		Entity closestEntity = null;
		double closestDistance = 999;
		for(Entity entity : world.getEntities()) {
			if(entity.getID() == player.getID()
					|| !(entity instanceof Animable))
				continue;
			if(entity instanceof Humanoid) {
				Humanoid otherPlayer = (Humanoid) entity;
				if(attackMod.isFriend(otherPlayer.getName())
						|| (attackMod.getTeam() != null && attackMod.getTeam()
								.isOnTeam(otherPlayer)))
					continue;
			}
			double distance = getDistanceBetween(player, entity);
			if((maxDistance <= 0 || distance <= maxDistance)
					&& distance < closestDistance) {
				closestEntity = entity;
				closestDistance = distance;
			}
		}
		return closestEntity;
	}

	public float getRotationXForShooting(Entity entity) {
		Player player = minecraft.getPlayer();
		double d = entity.getX() - player.getX();
		double d1 = entity.getZ() - player.getZ();
		return (float) ((Math.atan2(d1, d) * 180D) / 3.1415927410125732D) - 90F;
	}

	public float getRotationYForShooting(Entity entity) {
		Player player = minecraft.getPlayer();
		double dis1 = entity.getY() + 1 - player.getY() + 1;
		double dis2 = Math.sqrt(Math.pow(entity.getX() - player.getX(), 2)
				+ Math.pow(entity.getZ() - player.getZ(), 2));
		System.out.println(Math.pow(getDistanceBetween(player, entity) / 5, 2));
		return (float) ((Math.atan2(dis2, dis1) * 180D) / 3.1415927410125732D)
				- 80F
				- ((float) Math.pow(getDistanceBetween(player, entity) / 5, 2));
	}

	public float getDifference(float f1, float f2) {
		if(f1 == f2)
			return 0;
		if(f1 < f2)
			return Math.abs(f2 - f1);
		return Math.abs(f1 - f2);
	}
}
