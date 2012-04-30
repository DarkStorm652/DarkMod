package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet30EntityPosition extends Packet {
	public int getEntityID();

	public void setEntityID(int id);

	public byte getX();

	public void setX(byte x);

	public byte getY();

	public void setY(byte Y);

	public byte getZ();

	public void setZ(byte Z);

	public byte getRotationX();

	public void setRotationX(byte RotationX);

	public byte getRotationY();

	public void setRotationY(byte RotationY);

	public boolean isRotating();

	public void setRotating(boolean rotating);
}
