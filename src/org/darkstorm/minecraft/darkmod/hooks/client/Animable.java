package org.darkstorm.minecraft.darkmod.hooks.client;

public interface Animable extends Entity {
	public int getHealth();

	public void setHealth(int health);

	public int getAttackTimer();

	public void setAttackTimer(int attackTime);

	public int getHurtTimer();

	public void setHurtTimer(int hurtTime);

	public int getDeathTimer();

	public void setDeathTimer(int deathTime);
}
