package org.darkstorm.minecraft.darkmod.hooks.client;

public interface PlayerAbilities {
	public boolean isAbleToFly();

	public void setAbleToFly(boolean ableToFly);

	public boolean isFlying();

	public void setFlying(boolean flying);

	public boolean isInvulnerable();

	public void setInvulnerable(boolean invulnerable);

	public boolean isInstantBuilding();

	public void setInstantBuilding(boolean instantBuilding);
}
