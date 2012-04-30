package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.*;

public interface Packet15BlockPlace extends Packet {
	public int getX();

	public void setX(int x);

	public int getY();

	public void setY(int y);

	public int getZ();

	public void setZ(int z);

	public int getDirection();

	public void setDirection(int direction);

	public InventoryItem getItem();

	public void setItem(InventoryItem item);
}
