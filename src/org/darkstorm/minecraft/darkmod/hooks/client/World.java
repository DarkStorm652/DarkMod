package org.darkstorm.minecraft.darkmod.hooks.client;

import java.util.List;

public interface World {
	public List<Humanoid> getPlayers();

	public List<Entity> getEntities();

	public int getBlockIDAt(int x, int y, int z);

	public boolean setBlockAt(int x, int y, int z, int id);

	public WorldInfo getWorldInfo();

	public void setWorldInfo(WorldInfo worldInfo);
}
