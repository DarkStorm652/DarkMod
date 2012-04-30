package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet0KeepAlive extends Packet {
	public int getRandomID();

	public void setRandomID(int id);
}
