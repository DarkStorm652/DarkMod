package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet3Chat extends Packet {
	public String getMessage();

	public void setMessage(String message);
}
