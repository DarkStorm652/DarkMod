package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet14BlockDig extends Packet {
	public int getX();

	public void setX(int x);

	public int getY();

	public void setY(int y);

	public int getZ();

	public void setZ(int z);

	public int getFace();

	public void setFace(int face);

	public int getStatus();

	public void setStatus(int status);
}
