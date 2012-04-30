package org.darkstorm.minecraft.darkmod.hooks.client;

public interface Humanoid extends Animable {
	public String getName();

	public Inventory getInventory();

	public PlayerAbilities getAbilities();
}
