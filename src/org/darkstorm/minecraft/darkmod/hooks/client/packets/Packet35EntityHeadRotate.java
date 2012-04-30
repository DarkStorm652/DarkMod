package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet35EntityHeadRotate extends Packet {
	public int getEntityID();

	public void setEntityID(int id);

	public byte getRotation();

	public void setRotation(byte rotation);
}
