package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet18Animation extends Packet {
	public int getEntityID();

	public void setEntityID(int id);

	public int getState();

	public void setState(int state);
}
