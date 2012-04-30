package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet20NamedEntitySpawn extends Packet {
	public int getEntityID();

	public void setEntityID(int id);

	public String getName();

	public void setName(String name);

	public byte getRotationX();

	public void setRotationX(byte rotationX);

	public byte getRotationY();

	public void setRotationY(byte rotationY);

	public int getHeldItemID();

	public void setHeldItemID(int id);
}
