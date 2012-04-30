package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet16BlockItemSwitch extends Packet {
	public int getID();

	public void setID(int id);
}
