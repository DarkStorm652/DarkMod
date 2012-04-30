package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet130UpdateSign extends Packet {
	public int getX();

	public void setX(int x);

	public int getY();

	public void setY(int y);

	public int getZ();

	public void setZ(int z);

	public String[] getLines();

	public void setLines(String[] lines);
}
