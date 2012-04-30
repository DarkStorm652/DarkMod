package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet4UpdateTime extends Packet {
	public long getTime();

	public void setTime(long time);
}
