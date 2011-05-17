package org.darkstorm.minecraft.darkmod.hooks.client;

public interface BlockDigPacket extends Packet {
	public int getX();

	public void setX(int x);

	public int getY();

	public void setY(int y);

	public int getZ();

	public void setZ(int z);

	public int getBlockID();

	public void setBlockID(int blockID);

	public int getStatus();

	public void setStatus(int status);
}
