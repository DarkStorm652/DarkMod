package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.*;

public interface Packet9Respawn extends Packet {
	public int getRespawnDimension();

	public void setRespawnDimension(int respawnDimension);

	public int getDifficulty();

	public void setDifficulty(int difficulty);

	public int getWorldHeight();

	public void setWorldHeight(int worldHeight);

	public int getCreativeMode();

	public void setCreativeMode(int creativeMode);

	public WorldType getWorldType();

	public void setWorldType(WorldType worldType);
}
