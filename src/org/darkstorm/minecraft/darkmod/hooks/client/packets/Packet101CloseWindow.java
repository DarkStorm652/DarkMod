package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet101CloseWindow extends Packet {
	public int getWindowID();

	public void setWindowID(int windowID);
}
