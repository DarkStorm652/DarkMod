package org.darkstorm.minecraft.darkmod.hooks.client;

public interface InventoryItem {
	public int getID();

	public void setID(int id);

	public int getStackCount();

	public void setStackCount(int stackCount);

	public int getDamage();

	public void setDamage(int damage);
}
