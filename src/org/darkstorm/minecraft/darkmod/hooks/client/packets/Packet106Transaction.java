package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet106Transaction extends Packet {
	public int getWindowID();

	public void setWindowID(int windowID);

	public short getShortWindowID();

	public void setShortWindowID(short windowID);

	public boolean isAccepted();

	public void setAccepted(boolean accepted);
}
