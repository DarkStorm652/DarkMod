package org.darkstorm.minecraft.darkmod.hooks.client;

import java.util.List;

public interface World {
	public List<Humanoid> getPlayers();

	public List<Entity> getEntities();

	public long getTime();

	public long setTime(long time);

	public int getSpawnX();

	public int setSpawnX(int spawnX);

	public int getSpawnY();

	public int setSpawnY(int spawnY);

	public int getSpawnZ();

	public int setSpawnZ(int spawnZ);

	public int getBlockIDAt(int x, int y, int z);

	public boolean setBlockAt(int x, int y, int z, int id);
}
