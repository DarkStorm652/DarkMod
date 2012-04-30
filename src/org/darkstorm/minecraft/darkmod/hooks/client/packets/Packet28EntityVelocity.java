package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet28EntityVelocity extends Packet {
	public int getEntityID();

	public void setEntityID(int entityID);

	public int getSpeedX();

	public void setSpeedX(int speedX);

	public int getSpeedY();

	public void setSpeedY(int speedY);

	public int getSpeedZ();

	public void setSpeedZ(int speedZ);
}
