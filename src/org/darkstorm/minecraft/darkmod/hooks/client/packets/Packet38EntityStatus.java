package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet38EntityStatus extends Packet {
	public int getEntityID();

	public void setEntityID(int id);

	public byte getStatus();

	public void setStatus(byte status);
}
