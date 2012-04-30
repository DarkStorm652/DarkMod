package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet7UseEntity extends Packet {
	public int getPlayerEntityID();

	public void setPlayerEntityID(int id);

	public int getEntityID();

	public void setEntityID(int id);

	public int getButton();

	public void setButton(int button);
}
